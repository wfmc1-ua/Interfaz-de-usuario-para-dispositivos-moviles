package com.redes.simreader

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST = 100
    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    private lateinit var tvSimInfo: TextView
    private lateinit var tvCellInfo: TextView
    private lateinit var tvRestrictedData: TextView
    private lateinit var tvNeighborCells: TextView
    private lateinit var tvLastUpdate: TextView

    private lateinit var telephonyManager: TelephonyManager
    private var telephonyCallback: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.rootLayout)) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        tvSimInfo = findViewById(R.id.tvSimInfo)
        tvCellInfo = findViewById(R.id.tvCellInfo)
        tvRestrictedData = findViewById(R.id.tvRestrictedData)
        tvNeighborCells = findViewById(R.id.tvNeighborCells)
        tvLastUpdate = findViewById(R.id.tvLastUpdate)

        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        val missing = requiredPermissions.filter {
            ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missing.isEmpty()) {
            loadData()
            registerLiveUpdate()
        } else {
            ActivityCompat.requestPermissions(this, missing.toTypedArray(), PERMISSIONS_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST) {
            loadData()
            if (hasPermission(Manifest.permission.READ_PHONE_STATE)) {
                registerLiveUpdate()
            }
        }
    }

    private fun hasPermission(perm: String) =
        ActivityCompat.checkSelfPermission(this, perm) == PackageManager.PERMISSION_GRANTED

    private fun loadData() {
        loadSimInfo()
        loadCellInfo()
        loadRestrictedData()
        tvLastUpdate.text = "Actualizado: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}"
    }

    // ── SIM y Operador ────────────────────────────────────────────────────────

    private fun loadSimInfo() {
        if (!hasPermission(Manifest.permission.READ_PHONE_STATE)) {
            tvSimInfo.text = "Permiso READ_PHONE_STATE no concedido"
            return
        }

        val sb = StringBuilder()

        try {
            val sm = getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val subs = sm.activeSubscriptionInfoList

            if (!subs.isNullOrEmpty()) {
                for ((idx, sub) in subs.withIndex()) {
                    if (subs.size > 1) sb.appendLine("── SIM ${idx + 1} ──")

                    sb.appendLine("Operador SIM: ${sub.carrierName}")

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        sb.appendLine("MCC: ${sub.mccString ?: "N/D"}")
                        sb.appendLine("MNC: ${sub.mncString ?: "N/D"}")
                    } else {
                        @Suppress("DEPRECATION")
                        sb.appendLine("MCC: ${sub.mcc}")
                        @Suppress("DEPRECATION")
                        sb.appendLine("MNC: ${sub.mnc}")
                    }
                    sb.appendLine("País (ISO): ${sub.countryIso.uppercase().ifEmpty { "N/D" }}")

                    val tmSub = telephonyManager.createForSubscriptionId(sub.subscriptionId)
                    sb.appendLine("Operador red: ${tmSub.networkOperatorName.ifEmpty { "Desconocido" }}")
                    sb.appendLine("Itinerancia: ${if (tmSub.isNetworkRoaming) "Sí" else "No"}")
                    sb.appendLine("Tecnología datos: ${networkTypeName(tmSub.dataNetworkType)}")

                    if (idx < subs.size - 1) sb.appendLine()
                }
            } else {
                appendSimFallback(sb)
            }
        } catch (e: Exception) {
            appendSimFallback(sb)
        }

        tvSimInfo.text = sb.toString().trimEnd()
    }

    private fun appendSimFallback(sb: StringBuilder) {
        val op = telephonyManager.simOperator ?: ""
        val mcc = if (op.length >= 3) op.substring(0, 3) else "N/D"
        val mnc = if (op.length >= 3) op.substring(3) else "N/D"
        sb.appendLine("Operador SIM: ${telephonyManager.simOperatorName.ifEmpty { "Desconocido" }}")
        sb.appendLine("MCC: $mcc")
        sb.appendLine("MNC: $mnc")
        sb.appendLine("País (ISO): ${telephonyManager.simCountryIso.uppercase().ifEmpty { "N/D" }}")
        sb.appendLine("Operador red: ${telephonyManager.networkOperatorName.ifEmpty { "Desconocido" }}")
        sb.appendLine("Itinerancia: ${if (telephonyManager.isNetworkRoaming) "Sí" else "No"}")
        sb.appendLine("Tecnología datos: ${networkTypeName(telephonyManager.dataNetworkType)}")
    }

    private fun networkTypeName(type: Int): String = when (type) {
        TelephonyManager.NETWORK_TYPE_GPRS   -> "GPRS (2G)"
        TelephonyManager.NETWORK_TYPE_EDGE   -> "EDGE (2G)"
        TelephonyManager.NETWORK_TYPE_GSM    -> "GSM (2G)"
        TelephonyManager.NETWORK_TYPE_CDMA   -> "CDMA (2G)"
        TelephonyManager.NETWORK_TYPE_1xRTT  -> "1xRTT (2G)"
        TelephonyManager.NETWORK_TYPE_IDEN   -> "iDEN (2G)"
        TelephonyManager.NETWORK_TYPE_UMTS   -> "UMTS (3G)"
        TelephonyManager.NETWORK_TYPE_HSDPA  -> "HSDPA (3G)"
        TelephonyManager.NETWORK_TYPE_HSUPA  -> "HSUPA (3G)"
        TelephonyManager.NETWORK_TYPE_HSPA   -> "HSPA (3G)"
        TelephonyManager.NETWORK_TYPE_HSPAP  -> "HSPA+ (3G)"
        TelephonyManager.NETWORK_TYPE_EVDO_0 -> "EVDO rev. 0 (3G)"
        TelephonyManager.NETWORK_TYPE_EVDO_A -> "EVDO rev. A (3G)"
        TelephonyManager.NETWORK_TYPE_EVDO_B -> "EVDO rev. B (3G)"
        TelephonyManager.NETWORK_TYPE_EHRPD  -> "eHRPD (3G)"
        TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "TD-SCDMA (3G)"
        TelephonyManager.NETWORK_TYPE_LTE    -> "LTE (4G)"
        TelephonyManager.NETWORK_TYPE_IWLAN  -> "IWLAN (Wi-Fi calling)"
        TelephonyManager.NETWORK_TYPE_NR     -> "NR (5G)"
        else -> "Desconocido (tipo $type)"
    }



    private fun loadCellInfo() {
        val hasLocation = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val hasPhone = hasPermission(Manifest.permission.READ_PHONE_STATE)

        if (!hasLocation || !hasPhone) {
            val msg = "Se necesita permiso de ubicación precisa para leer la celda.\n(Actívalo en Ajustes > Permisos)"
            tvCellInfo.text = msg
            tvNeighborCells.text = msg
            return
        }

        val cells = try {
            telephonyManager.allCellInfo
        } catch (e: SecurityException) {
            null
        }

        if (cells.isNullOrEmpty()) {
            val msg = "Sin datos de celda.\nActiva la ubicación del dispositivo e inténtalo de nuevo."
            tvCellInfo.text = msg
            tvNeighborCells.text = "No hay celdas vecinas disponibles"
            return
        }

        val serving = cells.filter { it.isRegistered }
        val neighbors = cells.filter { !it.isRegistered }

        // celdas
        val sbServing = StringBuilder()
        val servingList = serving.ifEmpty { listOf(cells.first()) }
        for (cell in servingList) sbServing.append(formatCellFull(cell))
        tvCellInfo.text = sbServing.toString().trimEnd()

        // celdas vecinas
        if (neighbors.isEmpty()) {
            tvNeighborCells.text = "No se encontraron celdas vecinas"
        } else {
            tvNeighborCells.text = neighbors.joinToString("\n") { formatCellShort(it) }
        }
    }

    private fun formatCellFull(cell: CellInfo): String {
        val sb = StringBuilder()
        when (cell) {
            is CellInfoGsm -> {
                val id = cell.cellIdentity
                val sig = cell.cellSignalStrength
                sb.appendLine("Tipo: GSM (2G)")
                sb.appendLine("Cell ID: ${id.cid.takeIf { it != Int.MAX_VALUE } ?: "N/D"}")
                sb.appendLine("LAC: ${id.lac.takeIf { it != Int.MAX_VALUE } ?: "N/D"}")
                sb.appendLine("Señal: ${sig.dbm} dBm")
            }
            is CellInfoWcdma -> {
                val id = cell.cellIdentity
                val sig = cell.cellSignalStrength
                sb.appendLine("Tipo: WCDMA (3G)")
                sb.appendLine("Cell ID: ${id.cid.takeIf { it != Int.MAX_VALUE } ?: "N/D"}")
                sb.appendLine("LAC: ${id.lac.takeIf { it != Int.MAX_VALUE } ?: "N/D"}")
                sb.appendLine("Señal: ${sig.dbm} dBm")
            }
            is CellInfoLte -> {
                val id = cell.cellIdentity
                val sig = cell.cellSignalStrength
                sb.appendLine("Tipo: LTE (4G)")
                sb.appendLine("Cell ID (CI): ${id.ci.takeIf { it != Int.MAX_VALUE } ?: "N/D"}")
                sb.appendLine("TAC: ${id.tac.takeIf { it != Int.MAX_VALUE } ?: "N/D"}")
                sb.appendLine("Señal: ${sig.dbm} dBm")
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cell is CellInfoNr) {
                    val id = cell.cellIdentity as CellIdentityNr
                    val sig = cell.cellSignalStrength as CellSignalStrengthNr
                    sb.appendLine("Tipo: NR (5G)")
                    sb.appendLine("Cell ID (NCI): ${id.nci.takeIf { it != Long.MAX_VALUE } ?: "N/D"}")
                    sb.appendLine("TAC: ${id.tac.takeIf { it != Int.MAX_VALUE } ?: "N/D"}")
                    sb.appendLine("Señal: ${sig.dbm} dBm")
                } else {
                    sb.appendLine("Tipo celda desconocido")
                }
            }
        }
        return sb.toString()
    }

    private fun formatCellShort(cell: CellInfo): String = when (cell) {
        is CellInfoGsm -> "GSM  | CID: ${cell.cellIdentity.cid} | ${cell.cellSignalStrength.dbm} dBm"
        is CellInfoWcdma -> "WCDMA | CID: ${cell.cellIdentity.cid} | ${cell.cellSignalStrength.dbm} dBm"
        is CellInfoLte -> "LTE  | CI: ${cell.cellIdentity.ci} | ${cell.cellSignalStrength.dbm} dBm"
        else -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && cell is CellInfoNr) {
                val id = cell.cellIdentity as CellIdentityNr
                val sig = cell.cellSignalStrength as CellSignalStrengthNr
                "NR   | NCI: ${id.nci} | ${sig.dbm} dBm"
            } else "Celda desconocida"
        }
    }


    private fun loadRestrictedData() {
        val sb = StringBuilder()

        // IMEI
        sb.append("IMEI: ")
        try {
            val imei = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                telephonyManager.imei
            } else {
                @Suppress("DEPRECATION")
                telephonyManager.deviceId
            }
            sb.appendLine(imei ?: "null (dispositivo no informa)")
        } catch (e: SecurityException) {
            sb.appendLine("⚠ Restringido desde Android 10\n   (identificador permanente reservado a apps del sistema)")
        } catch (e: Exception) {
            sb.appendLine("Error: ${e.message}")
        }

        // IMSI
        sb.append("IMSI: ")
        try {
            @Suppress("DEPRECATION")
            val imsi = telephonyManager.subscriberId
            sb.appendLine(imsi ?: "null")
        } catch (e: SecurityException) {
            sb.appendLine("⚠ Restringido desde Android 10\n   (getSubscriberId requiere privilegio de sistema)")
        } catch (e: Exception) {
            sb.appendLine("Error: ${e.message}")
        }

        // ICCID
        sb.append("ICCID: ")
        try {
            @Suppress("DEPRECATION")
            val iccid = telephonyManager.simSerialNumber
            sb.appendLine(iccid ?: "null")
        } catch (e: SecurityException) {
            sb.appendLine("⚠ Restringido desde Android 10\n   (getSimSerialNumber requiere privilegio de sistema)")
        } catch (e: Exception) {
            sb.appendLine("Error: ${e.message}")
        }

        tvRestrictedData.text = sb.toString().trimEnd()
    }

    // ── Actualización en vivo ─────────────────────────────────────────────────

    @Suppress("DEPRECATION")
    private fun registerLiveUpdate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val cb = object : TelephonyCallback(), TelephonyCallback.CellInfoListener {
                override fun onCellInfoChanged(cellInfo: MutableList<CellInfo>) {
                    runOnUiThread { loadData() }
                }
            }
            telephonyCallback = cb
            telephonyManager.registerTelephonyCallback(Executors.newSingleThreadExecutor(), cb)
        } else {
            val listener = object : PhoneStateListener() {
                override fun onCellInfoChanged(cellInfo: MutableList<CellInfo>?) {
                    runOnUiThread { loadCellInfo(); updateTimestamp() }
                }

                override fun onDataConnectionStateChanged(state: Int, networkType: Int) {
                    runOnUiThread { loadSimInfo(); updateTimestamp() }
                }
            }
            telephonyCallback = listener
            telephonyManager.listen(
                listener,
                PhoneStateListener.LISTEN_CELL_INFO or PhoneStateListener.LISTEN_DATA_CONNECTION_STATE
            )
        }
    }

    private fun updateTimestamp() {
        tvLastUpdate.text = "Actualizado: ${SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())}"
    }

    @Suppress("DEPRECATION")
    override fun onDestroy() {
        super.onDestroy()
        telephonyCallback?.let { cb ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                telephonyManager.unregisterTelephonyCallback(cb as TelephonyCallback)
            } else {
                telephonyManager.listen(cb as PhoneStateListener, PhoneStateListener.LISTEN_NONE)
            }
        }
    }
}

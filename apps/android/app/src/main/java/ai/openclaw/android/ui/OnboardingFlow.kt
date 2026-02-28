package ai.openclaw.android.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import ai.openclaw.android.LocationMode
import ai.openclaw.android.MainViewModel
import ai.openclaw.android.R
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

private enum class OnboardingStep(val index: Int, val label: String) {
  GuestWelcome(1, "GuestWelcome"),
  GuestGateway(2, "GuestGateway"),
  GuestPermissions(3, "GuestPermissions"),
  GuestFinalCheck(4, "连接"),
}

private enum class GuestGatewayInputMode {
  SetupCode,
  Manual,
}

private val onboardingBackgroundGradient =
  listOf(
    Color(0xFFFFFFFF),
    Color(0xFFF7F8FA),
    Color(0xFFEFF1F5),
  )
private val onboardingSurface = Color(0xFFF6F7FA)
private val onboardingBorder = Color(0xFFE5E7EC)
private val onboardingBorderStrong = Color(0xFFD6DAE2)
private val onboardingText = Color(0xFF17181C)
private val onboardingTextSecondary = Color(0xFF4D5563)
private val onboardingTextTertiary = Color(0xFF8A92A2)
private val onboardingAccent = Color(0xFF1D5DD8)
private val onboardingAccentSoft = Color(0xFFECF3FF)
private val onboardingSuccess = Color(0xFF2F8C5A)
private val onboardingWarning = Color(0xFFC8841A)
private val onboardingCommandBg = Color(0xFF15171B)
private val onboardingCommandBorder = Color(0xFF2B2E35)
private val onboardingCommandAccent = Color(0xFF3FC97A)
private val onboardingCommandText = Color(0xFFE8EAEE)

private val onboardingFontFamily =
  FontFamily(
    Font(resId = R.font.manrope_400_regular, weight = FontWeight.Normal),
    Font(resId = R.font.manrope_500_medium, weight = FontWeight.Medium),
    Font(resId = R.font.manrope_600_semibold, weight = FontWeight.SemiBold),
    Font(resId = R.font.manrope_700_bold, weight = FontWeight.Bold),
  )

private val onboardingDisplayStyle =
  TextStyle(
    fontFamily = onboardingFontFamily,
    fontWeight = FontWeight.Bold,
    fontSize = 34.sp,
    lineHeight = 40.sp,
    letterSpacing = (-0.8).sp,
  )

private val onboardingTitle1Style =
  TextStyle(
    fontFamily = onboardingFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 24.sp,
    lineHeight = 30.sp,
    letterSpacing = (-0.5).sp,
  )

private val onboardingHeadlineStyle =
  TextStyle(
    fontFamily = onboardingFontFamily,
    fontWeight = FontWeight.SemiBold,
    fontSize = 16.sp,
    lineHeight = 22.sp,
    letterSpacing = (-0.1).sp,
  )

private val onboardingBodyStyle =
  TextStyle(
    fontFamily = onboardingFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 15.sp,
    lineHeight = 22.sp,
  )

private val onboardingCalloutStyle =
  TextStyle(
    fontFamily = onboardingFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    lineHeight = 20.sp,
  )

private val onboardingCaption1Style =
  TextStyle(
    fontFamily = onboardingFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 12.sp,
    lineHeight = 16.sp,
    letterSpacing = 0.2.sp,
  )

private val onboardingCaption2Style =
  TextStyle(
    fontFamily = onboardingFontFamily,
    fontWeight = FontWeight.Medium,
    fontSize = 11.sp,
    lineHeight = 14.sp,
    letterSpacing = 0.4.sp,
  )

@Composable
fun OnboardingFlow(viewModel: MainViewModel, modifier: Modifier = Modifier) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val statusText by viewModel.statusText.collectAsState()
  val isConnected by viewModel.isConnected.collectAsState()
  val serverName by viewModel.serverName.collectAsState()
  val remoteAddress by viewModel.remoteAddress.collectAsState()
  val persistedGuestGatewayToken by viewModel.gatewayToken.collectAsState()
  val pendingTrust by viewModel.pendingGuestGatewayTrust.collectAsState()

  var step by rememberSaveable { mutableStateOf(OnboardingStep.GuestWelcome) }
  var setupCode by rememberSaveable { mutableStateOf("") }
  var gatewayUrl by rememberSaveable { mutableStateOf("") }
  var gatewayPassword by rememberSaveable { mutableStateOf("") }
  var gatewayInputMode by rememberSaveable { mutableStateOf(GuestGatewayInputMode.SetupCode) }
  var gatewayAdvancedOpen by rememberSaveable { mutableStateOf(false) }
  var manualHost by rememberSaveable { mutableStateOf("10.0.2.2") }
  var manualPort by rememberSaveable { mutableStateOf("18789") }
  var manualTls by rememberSaveable { mutableStateOf(false) }
  var gatewayError by rememberSaveable { mutableStateOf<String?>(null) }
  var attemptedConnect by rememberSaveable { mutableStateOf(false) }

  var enableDiscovery by rememberSaveable { mutableStateOf(true) }
  var enableNotifications by rememberSaveable { mutableStateOf(true) }
  var enableMicrophone by rememberSaveable { mutableStateOf(false) }
  var enableCamera by rememberSaveable { mutableStateOf(false) }
  var enableSms by rememberSaveable { mutableStateOf(false) }

  val smsAvailable =
    remember(context) {
      context.packageManager?.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) == true
    }

  val selectedGuestPermissions =
    remember(
      context,
      enableDiscovery,
      enableNotifications,
      enableMicrophone,
      enableCamera,
      enableSms,
      smsAvailable,
    ) {
      val requested = mutableListOf<String>()
      if (enableDiscovery) {
        requested += if (Build.VERSION.SDK_INT >= 33) Manifest.permission.NEARBY_WIFI_DEVICES else Manifest.permission.ACCESS_FINE_LOCATION
      }
      if (enableNotifications && Build.VERSION.SDK_INT >= 33) requested += Manifest.permission.POST_NOTIFICATIONS
      if (enableMicrophone) requested += Manifest.permission.RECORD_AUDIO
      if (enableCamera) requested += Manifest.permission.CAMERA
      if (enableSms && smsAvailable) requested += Manifest.permission.SEND_SMS
      requested.filterNot { isPermissionGranted(context, it) }
    }

  val enabledPermissionSummary =
    remember(enableDiscovery, enableNotifications, enableMicrophone, enableCamera, enableSms, smsAvailable) {
      val enabled = mutableListOf<String>()
      if (enableDiscovery) enabled += "GuestGateway discovery"
      if (Build.VERSION.SDK_INT >= 33 && enableNotifications) enabled += "通知"
      if (enableMicrophone) enabled += "麦克风"
      if (enableCamera) enabled += "摄像头"
      if (smsAvailable && enableSms) enabled += "短信"
      if (enabled.isEmpty()) "未选择" else enabled.joinToString(", ")
    }

  val permissionLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestMultipleGuestPermissions()) {
      step = OnboardingStep.GuestFinalCheck
    }

  val qrScanLauncher =
    rememberLauncherForActivityResult(ScanContract()) { result ->
      val contents = result.contents?.trim().orEmpty()
      if (contents.isEmpty()) {
        return@rememberLauncherForActivityResult
      }
      val scannedSetupCode = resolveScannedSetupCode(contents)
      if (scannedSetupCode == null) {
        gatewayError = "QR码不包含有效的设置码。"
        return@rememberLauncherForActivityResult
      }
      setupCode = scannedSetupCode
      gatewayInputMode = GuestGatewayInputMode.SetupCode
      gatewayError = null
      attemptedConnect = false
    }

  if (pendingTrust != null) {
    val prompt = pendingTrust!!
    AlertDialog(
      onDismissRequest = { viewModel.declineGuestGatewayTrustPrompt() },
      title = { Text("信任此网关？") },
      text = {
        Text(
          "First-time TLS connection.\n\nVerify this SHA-256 fingerprint before trusting:\n${prompt.fingerprintSha256}",
        )
      },
      confirmButton = {
        TextButton(onClick = { viewModel.acceptGuestGatewayTrustPrompt() }) {
          Text("信任并继续")
        }
      },
      dismissButton = {
        TextButton(onClick = { viewModel.declineGuestGatewayTrustPrompt() }) {
          Text("取消")
        }
      },
    )
  }

  Box(
    modifier =
      modifier
        .fillMaxSize()
        .background(Brush.verticalGradient(onboardingBackgroundGradient)),
  ) {
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .imePadding()
          .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
          .navigationBarsPadding()
          .padding(horizontal = 20.dp, vertical = 12.dp),
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      Column(
        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
      ) {
        Column(
          modifier = Modifier.padding(top = 12.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          Text(
            "首次运行",
            style = onboardingCaption1Style.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp),
            color = onboardingAccent,
          )
          Text(
            "OpenClaw\nMobile Setup",
            style = onboardingDisplayStyle.copy(lineHeight = 38.sp),
            color = onboardingText,
          )
          Text(
            "Step ${step.index} of 4",
            style = onboardingCaption1Style,
            color = onboardingAccent,
          )
        }
        StepRailWrap(current = step)

        when (step) {
          OnboardingStep.GuestWelcome -> GuestWelcomeStep()
          OnboardingStep.GuestGateway ->
            GuestGatewayStep(
              inputMode = gatewayInputMode,
              advancedOpen = gatewayAdvancedOpen,
              setupCode = setupCode,
              manualHost = manualHost,
              manualPort = manualPort,
              manualTls = manualTls,
              gatewayToken = persistedGuestGatewayToken,
              gatewayPassword = gatewayPassword,
              gatewayError = gatewayError,
              onScanQrClick = {
                gatewayError = null
                qrScanLauncher.launch(
                  ScanOptions().apply {
                    setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                    setPrompt("Scan OpenClaw onboarding QR")
                    setBeepEnabled(false)
                    setOrientationLocked(false)
                  },
                )
              },
              onAdvancedOpenChange = { gatewayAdvancedOpen = it },
              onInputModeChange = {
                gatewayInputMode = it
                gatewayError = null
              },
              onSetupCodeChange = {
                setupCode = it
                gatewayError = null
              },
              onManualHostChange = {
                manualHost = it
                gatewayError = null
              },
              onManualPortChange = {
                manualPort = it
                gatewayError = null
              },
              onManualTlsChange = { manualTls = it },
              onTokenChange = viewModel::setGuestGatewayToken,
              onPasswordChange = { gatewayPassword = it },
            )
          OnboardingStep.GuestPermissions ->
            GuestPermissionsStep(
              enableDiscovery = enableDiscovery,
              enableNotifications = enableNotifications,
              enableMicrophone = enableMicrophone,
              enableCamera = enableCamera,
              enableSms = enableSms,
              smsAvailable = smsAvailable,
              context = context,
              onDiscoveryChange = { enableDiscovery = it },
              onNotificationsChange = { enableNotifications = it },
              onMicrophoneChange = { enableMicrophone = it },
              onCameraChange = { enableCamera = it },
              onSmsChange = { enableSms = it },
            )
          OnboardingStep.GuestFinalCheck ->
            FinalStep(
              parsedGuestGateway = parseGuestGatewayEndpoint(gatewayUrl),
              statusText = statusText,
              isConnected = isConnected,
              serverName = serverName,
              remoteAddress = remoteAddress,
              attemptedConnect = attemptedConnect,
              enabledGuestPermissions = enabledPermissionSummary,
              methodLabel = if (gatewayInputMode == GuestGatewayInputMode.SetupCode) "QR / Setup Code" else "手动",
            )
        }
      }

      Spacer(Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        val backEnabled = step != OnboardingStep.GuestWelcome
        Surface(
          modifier = Modifier.size(52.dp),
          shape = RoundedCornerShape(14.dp),
          color = onboardingSurface,
          border = androidx.compose.foundation.BorderStroke(1.dp, if (backEnabled) onboardingBorderStrong else onboardingBorder),
        ) {
          IconButton(
            onClick = {
              step =
                when (step) {
                  OnboardingStep.GuestWelcome -> OnboardingStep.GuestWelcome
                  OnboardingStep.GuestGateway -> OnboardingStep.GuestWelcome
                  OnboardingStep.GuestPermissions -> OnboardingStep.GuestGateway
                  OnboardingStep.GuestFinalCheck -> OnboardingStep.GuestPermissions
                }
            },
            enabled = backEnabled,
          ) {
            Icon(
              Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "返回",
              tint = if (backEnabled) onboardingTextSecondary else onboardingTextTertiary,
            )
          }
        }

        when (step) {
          OnboardingStep.GuestWelcome -> {
            Button(
              onClick = { step = OnboardingStep.GuestGateway },
              modifier = Modifier.weight(1f).height(52.dp),
              shape = RoundedCornerShape(14.dp),
              colors =
                ButtonDefaults.buttonColors(
                  containerColor = onboardingAccent,
                  contentColor = Color.White,
                  disabledContainerColor = onboardingAccent.copy(alpha = 0.45f),
                  disabledContentColor = Color.White,
                ),
            ) {
              Text("下一步", style = onboardingHeadlineStyle.copy(fontWeight = FontWeight.Bold))
            }
          }
          OnboardingStep.GuestGateway -> {
            Button(
              onClick = {
                if (gatewayInputMode == GuestGatewayInputMode.SetupCode) {
                  val parsedSetup = decodeGuestGatewaySetupCode(setupCode)
                  if (parsedSetup == null) {
                    gatewayError = "请先扫描QR码，或使用高级设置。"
                    return@Button
                  }
                  val parsedGuestGateway = parseGuestGatewayEndpoint(parsedSetup.url)
                  if (parsedGuestGateway == null) {
                    gatewayError = "设置码的网关URL无效。"
                    return@Button
                  }
                  gatewayUrl = parsedSetup.url
                  parsedSetup.token?.let { viewModel.setGuestGatewayToken(it) }
                  gatewayPassword = parsedSetup.password.orEmpty()
                } else {
                  val manualUrl = composeGuestGatewayManualUrl(manualHost, manualPort, manualTls)
                  val parsedGuestGateway = manualUrl?.let(::parseGuestGatewayEndpoint)
                  if (parsedGuestGateway == null) {
                    gatewayError = "手动端点无效。"
                    return@Button
                  }
                  gatewayUrl = parsedGuestGateway.displayUrl
                }
                step = OnboardingStep.GuestPermissions
              },
              modifier = Modifier.weight(1f).height(52.dp),
              shape = RoundedCornerShape(14.dp),
              colors =
                ButtonDefaults.buttonColors(
                  containerColor = onboardingAccent,
                  contentColor = Color.White,
                  disabledContainerColor = onboardingAccent.copy(alpha = 0.45f),
                  disabledContentColor = Color.White,
                ),
            ) {
              Text("下一步", style = onboardingHeadlineStyle.copy(fontWeight = FontWeight.Bold))
            }
          }
          OnboardingStep.GuestPermissions -> {
            Button(
              onClick = {
                viewModel.setCameraEnabled(enableCamera)
                viewModel.setLocationMode(if (enableDiscovery) LocationMode.WhileUsing else LocationMode.Off)
                if (selectedGuestPermissions.isEmpty()) {
                  step = OnboardingStep.GuestFinalCheck
                } else {
                  permissionLauncher.launch(selectedGuestPermissions.toTypedArray())
                }
              },
              modifier = Modifier.weight(1f).height(52.dp),
              shape = RoundedCornerShape(14.dp),
              colors =
                ButtonDefaults.buttonColors(
                  containerColor = onboardingAccent,
                  contentColor = Color.White,
                  disabledContainerColor = onboardingAccent.copy(alpha = 0.45f),
                  disabledContentColor = Color.White,
                ),
            ) {
              Text("下一步", style = onboardingHeadlineStyle.copy(fontWeight = FontWeight.Bold))
            }
          }
          OnboardingStep.GuestFinalCheck -> {
            if (isConnected) {
              Button(
                onClick = { viewModel.setOnboardingCompleted(true) },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = onboardingAccent,
                    contentColor = Color.White,
                    disabledContainerColor = onboardingAccent.copy(alpha = 0.45f),
                    disabledContentColor = Color.White,
                  ),
              ) {
                Text("完成", style = onboardingHeadlineStyle.copy(fontWeight = FontWeight.Bold))
              }
            } else {
              Button(
                onClick = {
                  val parsed = parseGuestGatewayEndpoint(gatewayUrl)
                  if (parsed == null) {
                    step = OnboardingStep.GuestGateway
                    gatewayError = "网关URL无效。"
                    return@Button
                  }
                  val token = persistedGuestGatewayToken.trim()
                  val password = gatewayPassword.trim()
                  attemptedConnect = true
                  viewModel.setManualEnabled(true)
                  viewModel.setManualHost(parsed.host)
                  viewModel.setManualPort(parsed.port)
                  viewModel.setManualTls(parsed.tls)
                  if (token.isNotEmpty()) {
                    viewModel.setGuestGatewayToken(token)
                  }
                  viewModel.setGuestGatewayPassword(password)
                  viewModel.connectManual()
                },
                modifier = Modifier.weight(1f).height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = onboardingAccent,
                    contentColor = Color.White,
                    disabledContainerColor = onboardingAccent.copy(alpha = 0.45f),
                    disabledContentColor = Color.White,
                  ),
              ) {
                Text("连接", style = onboardingHeadlineStyle.copy(fontWeight = FontWeight.Bold))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun StepRailWrap(current: OnboardingStep) {
  Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    HorizontalDivider(color = onboardingBorder)
    StepRail(current = current)
    HorizontalDivider(color = onboardingBorder)
  }
}

@Composable
private fun StepRail(current: OnboardingStep) {
  val steps = OnboardingStep.entries
  Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
    steps.forEach { step ->
      val complete = step.index < current.index
      val active = step.index == current.index
      Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Box(
          modifier =
            Modifier
              .fillMaxWidth()
              .height(5.dp)
              .background(
                color =
                  when {
                    complete -> onboardingSuccess
                    active -> onboardingAccent
                    else -> onboardingBorder
                  },
                shape = RoundedCornerShape(999.dp),
              ),
        )
        Text(
          text = step.label,
          style = onboardingCaption2Style.copy(fontWeight = if (active) FontWeight.Bold else FontWeight.SemiBold),
          color = if (active) onboardingAccent else onboardingTextSecondary,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      }
    }
  }
}

@Composable
private fun GuestWelcomeStep() {
  StepShell(title = "您将获得") {
    Bullet("从一个移动界面控制网关和操作员聊天。")
    Bullet("使用设置码连接，并可通过CLI命令恢复配对。")
    Bullet("仅启用您需要的权限和功能。")
    Bullet("在进入应用前进行真实的连接检查。")
  }
}

@Composable
private fun GuestGatewayStep(
  inputMode: GuestGatewayInputMode,
  advancedOpen: Boolean,
  setupCode: String,
  manualHost: String,
  manualPort: String,
  manualTls: Boolean,
  gatewayToken: String,
  gatewayPassword: String,
  gatewayError: String?,
  onScanQrClick: () -> Unit,
  onAdvancedOpenChange: (Boolean) -> Unit,
  onInputModeChange: (GuestGatewayInputMode) -> Unit,
  onSetupCodeChange: (String) -> Unit,
  onManualHostChange: (String) -> Unit,
  onManualPortChange: (String) -> Unit,
  onManualTlsChange: (Boolean) -> Unit,
  onTokenChange: (String) -> Unit,
  onPasswordChange: (String) -> Unit,
) {
  val resolvedEndpoint = remember(setupCode) { decodeGuestGatewaySetupCode(setupCode)?.url?.let { parseGuestGatewayEndpoint(it)?.displayUrl } }
  val manualResolvedEndpoint = remember(manualHost, manualPort, manualTls) { composeGuestGatewayManualUrl(manualHost, manualPort, manualTls)?.let { parseGuestGatewayEndpoint(it)?.displayUrl } }

  StepShell(title = "GuestGateway Connection") {
    GuideBlock(title = "扫描 onboarding QR 码") {
      Text("在网关主机上运行：", style = onboardingCalloutStyle, color = onboardingTextSecondary)
      CommandBlock("openclaw qr")
      Text("然后用此设备扫描。", style = onboardingCalloutStyle, color = onboardingTextSecondary)
    }
    Button(
      onClick = onScanQrClick,
      modifier = Modifier.fillMaxWidth().height(48.dp),
      shape = RoundedCornerShape(12.dp),
      colors =
        ButtonDefaults.buttonColors(
          containerColor = onboardingAccent,
          contentColor = Color.White,
        ),
    ) {
      Text("扫描 QR 码", style = onboardingHeadlineStyle.copy(fontWeight = FontWeight.Bold))
    }
    if (!resolvedEndpoint.isNullOrBlank()) {
      Text("QR码已捕获。请查看下方端点。", style = onboardingCalloutStyle, color = onboardingSuccess)
      ResolvedEndpoint(endpoint = resolvedEndpoint)
    }

    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(12.dp),
      color = onboardingSurface,
      border = androidx.compose.foundation.BorderStroke(1.dp, onboardingBorderStrong),
      onClick = { onAdvancedOpenChange(!advancedOpen) },
    ) {
      Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
          Text("高级设置", style = onboardingHeadlineStyle, color = onboardingText)
          Text("粘贴设置码或手动输入主机/端口。", style = onboardingCaption1Style, color = onboardingTextSecondary)
        }
        Icon(
          imageVector = if (advancedOpen) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
          contentDescription = if (advancedOpen) "收起高级设置" else "展开高级设置",
          tint = onboardingTextSecondary,
        )
      }
    }

    AnimatedVisibility(visible = advancedOpen) {
      Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GuideBlock(title = "手动设置命令") {
          Text("在网关主机上运行：", style = onboardingCalloutStyle, color = onboardingTextSecondary)
          CommandBlock("openclaw qr --setup-code-only")
          CommandBlock("openclaw qr --json")
          Text(
            "--json 会输出 setupCode 和 gatewayUrl。",
            style = onboardingCalloutStyle,
            color = onboardingTextSecondary,
          )
          Text(
            "自动URL发现尚未连接。Android模拟器使用 10.0.2.2；真机需要局域网/Tailscale主机。",
            style = onboardingCalloutStyle,
            color = onboardingTextSecondary,
          )
        }
        GuestGatewayModeToggle(inputMode = inputMode, onInputModeChange = onInputModeChange)

        if (inputMode == GuestGatewayInputMode.SetupCode) {
          Text("设置码", style = onboardingCaption1Style.copy(letterSpacing = 0.9.sp), color = onboardingTextSecondary)
          OutlinedTextField(
            value = setupCode,
            onValueChange = onSetupCodeChange,
            placeholder = { Text("从 openclaw qr --setup-code-only 粘贴代码", color = onboardingTextTertiary, style = onboardingBodyStyle) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            textStyle = onboardingBodyStyle.copy(fontFamily = FontFamily.Monospace, color = onboardingText),
            shape = RoundedCornerShape(14.dp),
            colors =
              OutlinedTextFieldDefaults.colors(
                focusedContainerColor = onboardingSurface,
                unfocusedContainerColor = onboardingSurface,
                focusedBorderColor = onboardingAccent,
                unfocusedBorderColor = onboardingBorder,
                focusedTextColor = onboardingText,
                unfocusedTextColor = onboardingText,
                cursorColor = onboardingAccent,
              ),
          )
          if (!resolvedEndpoint.isNullOrBlank()) {
            ResolvedEndpoint(endpoint = resolvedEndpoint)
          }
        } else {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            QuickFillChip(label = "Android 模拟器", onClick = {
              onManualHostChange("10.0.2.2")
              onManualPortChange("18789")
              onManualTlsChange(false)
            })
            QuickFillChip(label = "本地主机", onClick = {
              onManualHostChange("127.0.0.1")
              onManualPortChange("18789")
              onManualTlsChange(false)
            })
          }

          Text("主机", style = onboardingCaption1Style.copy(letterSpacing = 0.9.sp), color = onboardingTextSecondary)
          OutlinedTextField(
            value = manualHost,
            onValueChange = onManualHostChange,
            placeholder = { Text("10.0.2.2", color = onboardingTextTertiary, style = onboardingBodyStyle) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            textStyle = onboardingBodyStyle.copy(color = onboardingText),
            shape = RoundedCornerShape(14.dp),
            colors =
              OutlinedTextFieldDefaults.colors(
                focusedContainerColor = onboardingSurface,
                unfocusedContainerColor = onboardingSurface,
                focusedBorderColor = onboardingAccent,
                unfocusedBorderColor = onboardingBorder,
                focusedTextColor = onboardingText,
                unfocusedTextColor = onboardingText,
                cursorColor = onboardingAccent,
              ),
          )

          Text("端口", style = onboardingCaption1Style.copy(letterSpacing = 0.9.sp), color = onboardingTextSecondary)
          OutlinedTextField(
            value = manualPort,
            onValueChange = onManualPortChange,
            placeholder = { Text("18789", color = onboardingTextTertiary, style = onboardingBodyStyle) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = onboardingBodyStyle.copy(fontFamily = FontFamily.Monospace, color = onboardingText),
            shape = RoundedCornerShape(14.dp),
            colors =
              OutlinedTextFieldDefaults.colors(
                focusedContainerColor = onboardingSurface,
                unfocusedContainerColor = onboardingSurface,
                focusedBorderColor = onboardingAccent,
                unfocusedBorderColor = onboardingBorder,
                focusedTextColor = onboardingText,
                unfocusedTextColor = onboardingText,
                cursorColor = onboardingAccent,
              ),
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
              Text("使用 TLS", style = onboardingHeadlineStyle, color = onboardingText)
              Text("切换到安全websocket (wss)。", style = onboardingCalloutStyle.copy(lineHeight = 18.sp), color = onboardingTextSecondary)
            }
            Switch(
              checked = manualTls,
              onCheckedChange = onManualTlsChange,
              colors =
                SwitchDefaults.colors(
                  checkedTrackColor = onboardingAccent,
                  uncheckedTrackColor = onboardingBorderStrong,
                  checkedThumbColor = Color.White,
                  uncheckedThumbColor = Color.White,
                ),
            )
          }

          Text("令牌 (可选)", style = onboardingCaption1Style.copy(letterSpacing = 0.9.sp), color = onboardingTextSecondary)
          OutlinedTextField(
            value = gatewayToken,
            onValueChange = onTokenChange,
            placeholder = { Text("token", color = onboardingTextTertiary, style = onboardingBodyStyle) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            textStyle = onboardingBodyStyle.copy(color = onboardingText),
            shape = RoundedCornerShape(14.dp),
            colors =
              OutlinedTextFieldDefaults.colors(
                focusedContainerColor = onboardingSurface,
                unfocusedContainerColor = onboardingSurface,
                focusedBorderColor = onboardingAccent,
                unfocusedBorderColor = onboardingBorder,
                focusedTextColor = onboardingText,
                unfocusedTextColor = onboardingText,
                cursorColor = onboardingAccent,
              ),
          )

          Text("密码 (可选)", style = onboardingCaption1Style.copy(letterSpacing = 0.9.sp), color = onboardingTextSecondary)
          OutlinedTextField(
            value = gatewayPassword,
            onValueChange = onPasswordChange,
            placeholder = { Text("password", color = onboardingTextTertiary, style = onboardingBodyStyle) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            textStyle = onboardingBodyStyle.copy(color = onboardingText),
            shape = RoundedCornerShape(14.dp),
            colors =
              OutlinedTextFieldDefaults.colors(
                focusedContainerColor = onboardingSurface,
                unfocusedContainerColor = onboardingSurface,
                focusedBorderColor = onboardingAccent,
                unfocusedBorderColor = onboardingBorder,
                focusedTextColor = onboardingText,
                unfocusedTextColor = onboardingText,
                cursorColor = onboardingAccent,
              ),
          )

          if (!manualResolvedEndpoint.isNullOrBlank()) {
            ResolvedEndpoint(endpoint = manualResolvedEndpoint)
          }
        }
      }
    }

    if (!gatewayError.isNullOrBlank()) {
      Text(gatewayError, color = onboardingWarning, style = onboardingCaption1Style)
    }
  }
}

@Composable
private fun GuideBlock(
  title: String,
  content: @Composable ColumnScope.() -> Unit,
) {
  Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
    Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(onboardingAccent.copy(alpha = 0.4f)))
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
      Text(title, style = onboardingHeadlineStyle, color = onboardingText)
      content()
    }
  }
}

@Composable
private fun GuestGatewayModeToggle(
  inputMode: GuestGatewayInputMode,
  onInputModeChange: (GuestGatewayInputMode) -> Unit,
) {
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
    GuestGatewayModeChip(
      label = "设置码",
      active = inputMode == GuestGatewayInputMode.SetupCode,
      onClick = { onInputModeChange(GuestGatewayInputMode.SetupCode) },
      modifier = Modifier.weight(1f),
    )
    GuestGatewayModeChip(
      label = "手动",
      active = inputMode == GuestGatewayInputMode.Manual,
      onClick = { onInputModeChange(GuestGatewayInputMode.Manual) },
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun GuestGatewayModeChip(
  label: String,
  active: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Button(
    onClick = onClick,
    modifier = modifier.height(40.dp),
    shape = RoundedCornerShape(12.dp),
    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
    colors =
      ButtonDefaults.buttonColors(
        containerColor = if (active) onboardingAccent else onboardingSurface,
        contentColor = if (active) Color.White else onboardingText,
      ),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (active) Color(0xFF184DAF) else onboardingBorderStrong),
  ) {
    Text(
      text = label,
      style = onboardingCaption1Style.copy(fontWeight = FontWeight.Bold),
    )
  }
}

@Composable
private fun QuickFillChip(
  label: String,
  onClick: () -> Unit,
) {
  TextButton(
    onClick = onClick,
    shape = RoundedCornerShape(999.dp),
    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 7.dp),
    colors =
      ButtonDefaults.textButtonColors(
        containerColor = onboardingAccentSoft,
        contentColor = onboardingAccent,
      ),
  ) {
    Text(label, style = onboardingCaption1Style.copy(fontWeight = FontWeight.SemiBold))
  }
}

@Composable
private fun ResolvedEndpoint(endpoint: String) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    HorizontalDivider(color = onboardingBorder)
    Text(
      "RESOLVED ENDPOINT",
      style = onboardingCaption2Style.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.7.sp),
      color = onboardingTextSecondary,
    )
    Text(
      endpoint,
      style = onboardingCalloutStyle.copy(fontFamily = FontFamily.Monospace),
      color = onboardingText,
    )
    HorizontalDivider(color = onboardingBorder)
  }
}

@Composable
private fun StepShell(
  title: String,
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(verticalArrangement = Arrangement.spacedBy(0.dp)) {
    HorizontalDivider(color = onboardingBorder)
    Column(modifier = Modifier.padding(vertical = 14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
      Text(title, style = onboardingTitle1Style, color = onboardingText)
      content()
    }
    HorizontalDivider(color = onboardingBorder)
  }
}

@Composable
private fun InlineDivider() {
  HorizontalDivider(color = onboardingBorder)
}

@Composable
private fun GuestPermissionsStep(
  enableDiscovery: Boolean,
  enableNotifications: Boolean,
  enableMicrophone: Boolean,
  enableCamera: Boolean,
  enableSms: Boolean,
  smsAvailable: Boolean,
  context: Context,
  onDiscoveryChange: (Boolean) -> Unit,
  onNotificationsChange: (Boolean) -> Unit,
  onMicrophoneChange: (Boolean) -> Unit,
  onCameraChange: (Boolean) -> Unit,
  onSmsChange: (Boolean) -> Unit,
) {
  val discoveryPermission = if (Build.VERSION.SDK_INT >= 33) Manifest.permission.NEARBY_WIFI_DEVICES else Manifest.permission.ACCESS_FINE_LOCATION
  StepShell(title = "GuestPermissions") {
    Text(
      "仅启用您现在需要的权限。您可以在设置中稍后更改所有内容。",
      style = onboardingCalloutStyle,
      color = onboardingTextSecondary,
    )
    PermissionToggleRow(
      title = "GuestGateway discovery",
      subtitle = if (Build.VERSION.SDK_INT >= 33) "附近设备" else "位置 (用于NSD)",
      checked = enableDiscovery,
      granted = isPermissionGranted(context, discoveryPermission),
      onCheckedChange = onDiscoveryChange,
    )
    InlineDivider()
    if (Build.VERSION.SDK_INT >= 33) {
      PermissionToggleRow(
        title = "通知",
        subtitle = "前台服务 + 提醒",
        checked = enableNotifications,
        granted = isPermissionGranted(context, Manifest.permission.POST_NOTIFICATIONS),
        onCheckedChange = onNotificationsChange,
      )
      InlineDivider()
    }
    PermissionToggleRow(
      title = "麦克风",
      subtitle = "语音标签转录",
      checked = enableMicrophone,
      granted = isPermissionGranted(context, Manifest.permission.RECORD_AUDIO),
      onCheckedChange = onMicrophoneChange,
    )
    InlineDivider()
    PermissionToggleRow(
      title = "摄像头",
      subtitle = "camera.snap 和 camera.clip",
      checked = enableCamera,
      granted = isPermissionGranted(context, Manifest.permission.CAMERA),
      onCheckedChange = onCameraChange,
    )
    if (smsAvailable) {
      InlineDivider()
      PermissionToggleRow(
        title = "短信",
        subtitle = "允许网关触发的短信发送",
        checked = enableSms,
        granted = isPermissionGranted(context, Manifest.permission.SEND_SMS),
        onCheckedChange = onSmsChange,
      )
    }
    Text("所有设置都可以在设置中稍后更改。", style = onboardingCalloutStyle, color = onboardingTextSecondary)
  }
}

@Composable
private fun PermissionToggleRow(
  title: String,
  subtitle: String,
  checked: Boolean,
  granted: Boolean,
  onCheckedChange: (Boolean) -> Unit,
) {
  Row(
    modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
      Text(title, style = onboardingHeadlineStyle, color = onboardingText)
      Text(subtitle, style = onboardingCalloutStyle.copy(lineHeight = 18.sp), color = onboardingTextSecondary)
      Text(
        if (granted) "已授权" else "未授权",
        style = onboardingCaption1Style,
        color = if (granted) onboardingSuccess else onboardingTextSecondary,
      )
    }
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors =
        SwitchDefaults.colors(
          checkedTrackColor = onboardingAccent,
          uncheckedTrackColor = onboardingBorderStrong,
          checkedThumbColor = Color.White,
          uncheckedThumbColor = Color.White,
        ),
    )
  }
}

@Composable
private fun FinalStep(
  parsedGuestGateway: GuestGatewayEndpointConfig?,
  statusText: String,
  isConnected: Boolean,
  serverName: String?,
  remoteAddress: String?,
  attemptedConnect: Boolean,
  enabledGuestPermissions: String,
  methodLabel: String,
) {
  StepShell(title = "Review") {
    SummaryField(label = "方式", value = methodLabel)
    SummaryField(label = "GuestGateway", value = parsedGuestGateway?.displayUrl ?: "无效的网关URL")
    SummaryField(label = "Enabled GuestPermissions", value = enabledGuestPermissions)

    if (!attemptedConnect) {
      Text("点击连接以验证网关可达性和认证。", style = onboardingCalloutStyle, color = onboardingTextSecondary)
    } else {
      Text("Status: $statusText", style = onboardingCalloutStyle, color = if (isConnected) onboardingSuccess else onboardingTextSecondary)
      if (isConnected) {
        Text("Connected to ${serverName ?: remoteAddress ?: "网关"}", style = onboardingCalloutStyle, color = onboardingSuccess)
      } else {
        GuideBlock(title = "需要配对") {
          Text("在网关主机上运行：", style = onboardingCalloutStyle, color = onboardingTextSecondary)
          CommandBlock("openclaw nodes pending")
          CommandBlock("openclaw nodes approve <requestId>")
          Text("Then tap Connect again.", style = onboardingCalloutStyle, color = onboardingTextSecondary)
        }
      }
    }
  }
}

@Composable
private fun SummaryField(label: String, value: String) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
      label,
      style = onboardingCaption2Style.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 0.6.sp),
      color = onboardingTextSecondary,
    )
    Text(value, style = onboardingHeadlineStyle, color = onboardingText)
    HorizontalDivider(color = onboardingBorder)
  }
}

@Composable
private fun CommandBlock(command: String) {
  Row(
    modifier =
      Modifier
        .fillMaxWidth()
        .background(onboardingCommandBg, RoundedCornerShape(12.dp))
        .border(width = 1.dp, color = onboardingCommandBorder, shape = RoundedCornerShape(12.dp)),
  ) {
    Box(modifier = Modifier.width(3.dp).height(42.dp).background(onboardingCommandAccent))
    Text(
      command,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
      style = onboardingCalloutStyle,
      fontFamily = FontFamily.Monospace,
      color = onboardingCommandText,
    )
  }
}

@Composable
private fun Bullet(text: String) {
  Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
    Box(
      modifier =
        Modifier
          .padding(top = 7.dp)
          .size(8.dp)
          .background(onboardingAccentSoft, CircleShape),
    )
    Box(
      modifier =
        Modifier
          .padding(top = 9.dp)
          .size(4.dp)
          .background(onboardingAccent, CircleShape),
    )
    Text(text, style = onboardingBodyStyle, color = onboardingTextSecondary, modifier = Modifier.weight(1f))
  }
}

private fun isPermissionGranted(context: Context, permission: String): Boolean {
  return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
}

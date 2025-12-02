import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import org.jetbrains.compose.ui.tooling.preview.Preview

// Colores del Mascota
val MascotBlue = Color(0xFF4A90E2)
val MascotBlueDark = Color(0xFF357ABD) // Para el borde/sombra 3D
val MascotOrange = Color(0xFFF57C00)
val MascotOrangeDark = Color(0xFFE65100) // Para el borde/sombra 3D
val CheckerLight = Color(0xFFFFFFFF)
val CheckerGray = Color(0xFFE0E0E0)
val CheckerGrayDark = Color(0xFFBDBDBD) // Para el borde 3D
val FaceColor = Color(0xFF2C3E50)

// Constantes para la forma
val BlockCornerRadius = 24.dp
val BlockSize = 120.dp // Tamaño base del cuadrado antes de la perspectiva
val LayerThickness = 12.dp // Grosor del efecto 3D

@Composable
fun CheckerboardPattern(modifier: Modifier = Modifier, squareSize: Float = 20f) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val cols = (size.width / squareSize).toInt() + 1
        val rows = (size.height / squareSize).toInt() + 1

        for (i in 0 until cols) {
            for (j in 0 until rows) {
                val color = if ((i + j) % 2 == 0) CheckerLight else CheckerGray
                drawRect(
                    color = color, topLeft = Offset(i * squareSize, j * squareSize), size = Size(squareSize, squareSize)
                )
            }
        }
    }
}

@Composable
fun MascotFace(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(60.dp, 30.dp)) {
        // Ojos
        val eyeRadius = 5.dp.toPx()
        val eyeSparkRadius = 2.dp.toPx()
        val eyeY = size.height * 0.3f
        drawCircle(FaceColor, radius = eyeRadius, center = Offset(size.width * 0.25f, eyeY))
        drawCircle(Color.White, radius = eyeSparkRadius, center = Offset(size.width * 0.22f, eyeY * 0.85f))
        drawCircle(FaceColor, radius = eyeRadius, center = Offset(size.width * 0.75f, eyeY))
        drawCircle(Color.White, radius = eyeSparkRadius, center = Offset(size.width * 0.72f, eyeY * 0.85f))

        // Boca (un pequeño arco)
        val mouthPadding = size.width * 0.35f
        drawArc(
            color = FaceColor,
            startAngle = 20f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(mouthPadding, size.height * 0.2f),
            size = Size(size.width - (mouthPadding * 2), size.height * 0.6f),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}

@Preview
@Composable
fun PreviewMascotFace(){
    MascotFace()
}

@Composable
fun BlockLayer(
    mainColor: Color,
    sideColor: Color,
    modifier: Modifier = Modifier,
    isCheckerboard: Boolean = false,
    content: @Composable BoxScope.() -> Unit = {}
) {
    // El truco para la perspectiva isométrica en 2D:
    // Rotar 45 grados en Z y luego "aplastar" en Y (scaleY).
    val isometricModifier = Modifier.graphicsLayer {
            rotationZ = 45f
            scaleX = 0.6f
            scaleY = 0.6f
        }

    Box(modifier = modifier.size(BlockSize)) {
        // 1. Dibujar el "grosor" (los lados oscuros)
        // Dibujamos varias capas desplazadas hacia abajo para dar volumen.
        val thicknessLayers = 10
        for (i in 1..thicknessLayers) {
            val offset = (LayerThickness.value / thicknessLayers) * i
            Box(
                modifier = Modifier.fillMaxSize().offset(y = offset.dp).then(isometricModifier)
                    .background(sideColor, RoundedCornerShape(BlockCornerRadius))
            )
        }

        // 2. Dibujar la "cara superior" (la superficie principal)
        Box(
            modifier = Modifier.fillMaxSize().then(isometricModifier).clip(RoundedCornerShape(BlockCornerRadius))
                .background(if (isCheckerboard) Color.Transparent else mainColor)
        ) {
            if (isCheckerboard) {
                CheckerboardPattern()
            }
            // Contenido adicional (como la cara) se renderiza aquí, sobre la superficie
            // Necesitamos contrarrestar la distorsión isométrica para que la cara se vea recta
            Box(
                modifier = Modifier.align(Alignment.Center).graphicsLayer {
                        scaleY = 1f / 0.75f // Invertir el aplastamiento
                        rotationZ = -45f   // Invertir la rotación
                    }) {
                content()
            }
        }
    }
}

@Composable
fun WeppyMascot(isSwapped: Boolean, onSwap: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    // InteractionSource necesario para un clickable sin efecto de onda (ripple) si lo deseas
    val interactionSource = remember { MutableInteractionSource() }

    // --- CONSTANTES DE POSICIÓN Y REBOTE ---
    val topPos = (-20).dp
    val bottomPos = 20.dp
    val midPos = 0.dp

    // El efecto acordeón: los extremos rebotan más.
    val outerBounceMax = 24.dp
    val innerBounceMax = 16.dp

    // --- ANIMACIÓN 1: El Rebote Infinito (Ya la teníamos) ---
    val infiniteTransition = rememberInfiniteTransition(label = "BounceInfinite")
    val bouncePhase by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse
        ), label = "BouncePhase"
    )

    // --- ANIMACIÓN 2: La Transición de Intercambio (Nueva) ---
    // Esta transición maneja el movimiento suave cuando 'isSwapped' cambia.
    val swapTransition = updateTransition(targetState = isSwapped, label = "SwapTransition")

    // Animamos la posición BASE del bloque AZUL.
    // Si está intercambiado (true), su base es abajo. Si no (false), su base es arriba.
    val blueBaseOffset by swapTransition.animateDp(
        label = "BlueOffsetAnim",
        transitionSpec = {
            tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            )
        }) { swapped -> if (swapped) bottomPos else topPos }

    // Animamos la posición BASE del bloque NARANJA (inverso al azul).
    val orangeBaseOffset by swapTransition.animateDp(
        label = "OrangeOffsetAnim",
        transitionSpec = {
            tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            )
        }) { swapped -> if (swapped) topPos else bottomPos }

    // --- FUNCIÓN HELPER PARA COMBINAR ANIMACIONES ---
    // Calcula la posición final sumando la base animada y restando el rebote actual.
    fun calculateFinalOffset(baseAnimatedOffset: Dp, maxBounce: Dp, phase: Float): Dp {
        return baseAnimatedOffset - (maxBounce * phase)
    }

    Box(
        modifier = modifier.fillMaxSize().clickable(
            interactionSource = interactionSource,
            indication = null, // 'null' quita el efecto visual de clic (ripple)
        ) {
            onSwap(!isSwapped) // Al hacer tap, notificamos al padre
        }, contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.offset(y = 6.dp)) {

            // Determinamos los Z-Index basados en el estado objetivo.
            // El que esté destinado a estar arriba recibe el Z más alto (3f).
            val blueZIndex = if (isSwapped) 1f else 3f
            val orangeZIndex = if (isSwapped) 3f else 1f

            // --- Bloque NARANJA (Variable) ---
            BlockLayer(
                mainColor = MascotOrange, sideColor = MascotOrangeDark, modifier = Modifier.align(Alignment.Center)
                    // Usamos la posición base animada del naranja
                    .offset(
                        y = calculateFinalOffset(
                            orangeBaseOffset, outerBounceMax, bouncePhase
                        )
                    ).zIndex(orangeZIndex)
            ) {
                // OPCIONAL: Si quieres que la cara siempre esté en el bloque superior,
                // deberías mover este bloque 'if' dentro del contenido del bloque Naranja también.
                if (isSwapped) {
                    MascotFace(modifier = Modifier.offset(y = (-5).dp))
                }
            }

            // --- Bloque MEDIO (Fijo en su base, solo rebota) ---
            BlockLayer(
                mainColor = Color.Transparent,
                sideColor = CheckerGrayDark,
                isCheckerboard = true,
                modifier = Modifier.align(Alignment.Center)
                    // Su base siempre es 'midPos' (20.dp)
                    .offset(
                        y = calculateFinalOffset(
                            midPos, innerBounceMax, bouncePhase
                        )
                    ).zIndex(2f) // Siempre en el medio
            )

            // --- Bloque AZUL (Variable) ---
            BlockLayer(
                mainColor = MascotBlue, sideColor = MascotBlueDark, modifier = Modifier.align(Alignment.Center)
                    // Usamos la posición base animada del azul
                    .offset(
                        y = calculateFinalOffset(
                            blueBaseOffset, outerBounceMax, bouncePhase
                        )
                    ).zIndex(blueZIndex)
            ) {
                // La cara se queda en el bloque azul.
                // Cuando el azul baja, la cara baja con él.
                MascotFace(modifier = Modifier.offset(y = (-5).dp))
            }
        }
    }
}

@Preview()
@Composable
fun InteractivePreview() {
    var isSwapped by remember { mutableStateOf(false) }
    WeppyMascot(isSwapped = isSwapped, onSwap = { isSwapped = !isSwapped })
}

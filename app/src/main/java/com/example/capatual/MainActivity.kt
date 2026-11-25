package com.example.capatual

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

// Cinza opaco confortável para os olhos (evita branco forte e preto estourado)
private val FundoCinzaOpaco = Color(0xFF1A1D24)
private val CardCinzaEscuro = Color(0xFF242832)
private val SuperficieCinza = Color(0xFF2E3440)
private val BordaCinza = Color(0xFF3C4252)
private val TextoClaro = Color(0xFFE6E9F0)
private val TextoSuave = Color(0xFFA7B0C2)
private val DestaqueSuave = Color(0xFF8C9EFF)

private val PaletaConfortavel = darkColorScheme(
    primary = DestaqueSuave,
    onPrimary = Color(0xFF141722),
    primaryContainer = Color(0xFF2B314A),
    onPrimaryContainer = TextoClaro,
    secondaryContainer = SuperficieCinza,
    onSecondaryContainer = TextoClaro,
    background = FundoCinzaOpaco,
    onBackground = TextoClaro,
    surface = CardCinzaEscuro,
    onSurface = TextoClaro,
    surfaceVariant = SuperficieCinza,
    onSurfaceVariant = TextoSuave,
    outlineVariant = BordaCinza,
    error = Color(0xFFFF7B7B)
)

data class OpcaoObra(
    val chaveId: String,
    val tituloPadrao: String,
    val tipo: String,
    val imagemRes: Int
)

data class ProgressoObra(
    val titulo: String,
    val capitulo: Int,
    val nota: String
)

data class LeituraSalva(
    val chaveId: String,
    val titulo: String,
    val tipo: String,
    val imagemRes: Int,
    val capitulo: Int,
    val nota: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = PaletaConfortavel) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = FundoCinzaOpaco,
                    contentColor = TextoClaro
                ) {
                    AppLeituras()
                }
            }
        }
    }
}

private const val PREFS_NAME = "capitulos_obras_prefs"
private const val KEY_OBRA_ATIVA = "obra_ativa_id"

private fun carregarProgressoDaObra(
    context: Context,
    chaveId: String,
    tituloPadrao: String
): ProgressoObra {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val titulo = prefs.getString("titulo_$chaveId", tituloPadrao) ?: tituloPadrao
    val capitulo = prefs.getInt("cap_$chaveId", 1)
    val nota = prefs.getString("nota_$chaveId", "") ?: ""
    return ProgressoObra(titulo = titulo, capitulo = capitulo, nota = nota)
}

private fun carregarLeituraAtiva(
    context: Context,
    opcoesObras: List<OpcaoObra>
): LeituraSalva {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    val chaveSalva = prefs.getString(KEY_OBRA_ATIVA, opcoesObras[0].chaveId) ?: opcoesObras[0].chaveId
    val opcao = opcoesObras.find { it.chaveId == chaveSalva } ?: opcoesObras[0]
    val progresso = carregarProgressoDaObra(context, opcao.chaveId, opcao.tituloPadrao)

    return LeituraSalva(
        chaveId = opcao.chaveId,
        titulo = progresso.titulo,
        tipo = opcao.tipo,
        imagemRes = opcao.imagemRes,
        capitulo = progresso.capitulo,
        nota = progresso.nota
    )
}

private fun salvarLeituraAtiva(context: Context, leitura: LeituraSalva) {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.edit()
        .putString(KEY_OBRA_ATIVA, leitura.chaveId)
        .putString("titulo_${leitura.chaveId}", leitura.titulo)
        .putInt("cap_${leitura.chaveId}", leitura.capitulo)
        .putString("nota_${leitura.chaveId}", leitura.nota)
        .apply()
}

@Composable
fun AppLeituras() {
    val context = LocalContext.current

    val opcoesObras = remember {
        listOf(
            OpcaoObra("one_piece", "One Piece", "Mangá", R.drawable.capa_manga),
            OpcaoObra("solo_leveling", "Solo Leveling", "Manhwa", R.drawable.capa_manhwa),
            OpcaoObra("martial_peak", "Martial Peak", "Manhua", R.drawable.capa_manhua),
            OpcaoObra("naruto", "Naruto", "Mangá", R.drawable.naruto_capa),
            OpcaoObra("baskerville", "Revenge of the Baskerville Bloodhound", "Manhwa", R.drawable.revenge_of_the_baskerville_bloodhound_capa),
            OpcaoObra("tbate", "The Beginning After The End", "Manhwa", R.drawable.the_beginning_after_the_end_capa)
        )
    }

    var leituraAtual by remember { mutableStateOf(carregarLeituraAtiva(context, opcoesObras)) }
    var telaAtual by remember { mutableStateOf("abertura") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoCinzaOpaco)
    ) {
        when (telaAtual) {
            "abertura" -> {
                TelaBoasVindas(
                    onIniciar = { telaAtual = "selecao" }
                )
            }

            "selecao" -> {
                TelaSelecaoCapitulos(
                    leituraAtual = leituraAtual,
                    opcoesObras = opcoesObras,
                    onCarregarProgressoObra = { opcao ->
                        carregarProgressoDaObra(context, opcao.chaveId, opcao.tituloPadrao)
                    },
                    onSalvarCapitulo = { novaLeitura ->
                        leituraAtual = novaLeitura
                        salvarLeituraAtiva(context, novaLeitura)
                    },
                    onAbrirCapitulo = { novaLeitura ->
                        leituraAtual = novaLeitura
                        salvarLeituraAtiva(context, novaLeitura)
                        telaAtual = "detalhe"
                    }
                )
            }

            else -> {
                TelaDetalheCapitulo(
                    leitura = leituraAtual,
                    onMaisUmCapitulo = {
                        val atualizada = leituraAtual.copy(capitulo = leituraAtual.capitulo + 1)
                        leituraAtual = atualizada
                        salvarLeituraAtiva(context, atualizada)
                    },
                    onVoltarSelecao = {
                        telaAtual = "selecao"
                    }
                )
            }
        }
    }
}

@Composable
fun TelaBoasVindas(
    onIniciar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoCinzaOpaco)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = CardCinzaEscuro),
            border = BorderStroke(1.dp, BordaCinza)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Onde Parei",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextoClaro,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Acompanhe seu capítulo atual em mangás, manhwas e manhuas em um visual escuro e confortável.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSuave,
                    textAlign = TextAlign.Center
                )

                Button(
                    onClick = onIniciar,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "Iniciar",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun TelaSelecaoCapitulos(
    leituraAtual: LeituraSalva,
    opcoesObras: List<OpcaoObra>,
    onCarregarProgressoObra: (OpcaoObra) -> ProgressoObra,
    onSalvarCapitulo: (LeituraSalva) -> Unit,
    onAbrirCapitulo: (LeituraSalva) -> Unit
) {
    val indiceInicial = remember {
        opcoesObras.indexOfFirst { it.chaveId == leituraAtual.chaveId }.coerceAtLeast(0)
    }
    val pagerState = rememberPagerState(
        initialPage = indiceInicial,
        pageCount = { opcoesObras.size }
    )

    var opcaoSelecionada by remember { mutableStateOf(opcoesObras[indiceInicial]) }
    var titulo by remember { mutableStateOf(leituraAtual.titulo) }
    var capituloTexto by remember { mutableStateOf(leituraAtual.capitulo.toString()) }
    var nota by remember { mutableStateOf(leituraAtual.nota) }
    var mensagemFeedback by remember { mutableStateOf<String?>(null) }
    var erroValidacao by remember { mutableStateOf(false) }

    val coresCampo = OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextoClaro,
        unfocusedTextColor = TextoClaro,
        focusedContainerColor = CardCinzaEscuro,
        unfocusedContainerColor = CardCinzaEscuro,
        focusedBorderColor = DestaqueSuave,
        unfocusedBorderColor = BordaCinza,
        focusedLabelColor = DestaqueSuave,
        unfocusedLabelColor = TextoSuave,
        cursorColor = DestaqueSuave
    )

    fun validarEConstruir(): LeituraSalva? {
        val tituloLimpo = titulo.trim()
        val capNumero = capituloTexto.trim().toIntOrNull()

        if (tituloLimpo.isEmpty()) {
            erroValidacao = true
            mensagemFeedback = "Digite o nome da obra antes de continuar."
            return null
        }
        if (capNumero == null || capNumero < 1) {
            erroValidacao = true
            mensagemFeedback = "Informe um capítulo válido (1 ou maior)."
            return null
        }

        erroValidacao = false
        return LeituraSalva(
            chaveId = opcaoSelecionada.chaveId,
            titulo = tituloLimpo,
            tipo = opcaoSelecionada.tipo,
            imagemRes = opcaoSelecionada.imagemRes,
            capitulo = capNumero,
            nota = nota.trim()
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoCinzaOpaco)
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Meus Capítulos",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextoClaro
        )

        Text(
            text = "Arraste para o lado e toque na capa para escolher:",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSuave
        )

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 76.dp),
            pageSpacing = 14.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val opcao = opcoesObras[page]
            val selecionada = opcao.chaveId == opcaoSelecionada.chaveId

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        opcaoSelecionada = opcao
                        val progressoSalvo = onCarregarProgressoObra(opcao)
                        titulo = progressoSalvo.titulo
                        capituloTexto = progressoSalvo.capitulo.toString()
                        nota = progressoSalvo.nota
                        mensagemFeedback = null
                        erroValidacao = false
                    },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = CardCinzaEscuro),
                border = BorderStroke(
                    width = if (selecionada) 2.dp else 1.dp,
                    color = if (selecionada) DestaqueSuave else BordaCinza
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = opcao.imagemRes),
                        contentDescription = opcao.tituloPadrao,
                        modifier = Modifier
                            .size(width = 125.dp, height = 175.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = opcao.tituloPadrao,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextoClaro,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = SuperficieCinza
                    ) {
                        Text(
                            text = opcao.tipo,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextoSuave
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(opcoesObras.size) { index ->
                val ativa = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (ativa) 9.dp else 7.dp)
                        .clip(CircleShape)
                        .background(if (ativa) DestaqueSuave else BordaCinza)
                )
            }
        }

        OutlinedTextField(
            value = titulo,
            onValueChange = {
                titulo = it
                mensagemFeedback = null
            },
            label = { Text("Nome da obra") },
            colors = coresCampo,
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = capituloTexto,
            onValueChange = {
                capituloTexto = it
                mensagemFeedback = null
            },
            label = { Text("Capítulo atual") },
            colors = coresCampo,
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = nota,
            onValueChange = {
                nota = it
                mensagemFeedback = null
            },
            label = { Text("Nota rápida (opcional)") },
            colors = coresCampo,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        )

        if (mensagemFeedback != null) {
            Text(
                text = mensagemFeedback!!,
                color = if (erroValidacao) Color(0xFFFF7B7B) else DestaqueSuave,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = {
                val validada = validarEConstruir()
                if (validada != null) {
                    onAbrirCapitulo(validada)
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Ver Capítulo")
        }

        OutlinedButton(
            onClick = {
                val validada = validarEConstruir()
                if (validada != null) {
                    onSalvarCapitulo(validada)
                    mensagemFeedback = "Capítulo salvo para ${validada.titulo}."
                }
            },
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BordaCinza),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Salvar Capítulo", color = TextoClaro)
        }
    }
}

@Composable
fun TelaDetalheCapitulo(
    leitura: LeituraSalva,
    onMaisUmCapitulo: () -> Unit,
    onVoltarSelecao: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(FundoCinzaOpaco)
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = leitura.titulo,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextoClaro,
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = CardCinzaEscuro),
            border = BorderStroke(1.dp, BordaCinza)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = leitura.imagemRes),
                    contentDescription = leitura.titulo,
                    modifier = Modifier
                        .size(width = 150.dp, height = 215.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Fit
                )

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = SuperficieCinza
                ) {
                    Text(
                        text = leitura.tipo,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = TextoSuave
                    )
                }

                Text(
                    text = "Capítulo atual: ${leitura.capitulo}",
                    style = MaterialTheme.typography.titleLarge,
                    color = DestaqueSuave,
                    fontWeight = FontWeight.Bold
                )

                if (leitura.nota.isNotBlank()) {
                    Text(
                        text = "Nota: ${leitura.nota}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSuave,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Button(
            onClick = onMaisUmCapitulo,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("+1 Capítulo")
        }

        OutlinedButton(
            onClick = onVoltarSelecao,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, BordaCinza),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Voltar para Meus Capítulos", color = TextoClaro)
        }
    }
}
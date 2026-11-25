\# Onde Parei — Acompanhamento de Capítulos



Aplicativo Android desenvolvido em \*\*Kotlin\*\* com \*\*Jetpack Compose\*\* para acompanhar o progresso de leitura em mangás, manhwas e manhuas de forma simples, local e confortável para os olhos.



\## Funcionalidades



\* \*\*Tela inicial de abertura ("Onde Parei"):\*\* Apresentação simples com botão \*\*Iniciar\*\* para acessar o catálogo.

\* \*\*Catálogo com carrossel (`HorizontalPager`):\*\* Permite deslizar entre obras de mangá, manhwa e manhua, visualizar capas em `res/drawable` e tocar para selecionar.

\* \*\*Progresso independente por obra:\*\* Cada obra mantém seu próprio número de capítulo e nota rápida salvos individualmente.

\* \*\*Incremento rápido e visualização de leitura:\*\* Tela de detalhe da obra com botão \*\*+1 Capítulo\*\* e retorno para \*\*Meus Capítulos\*\*.

\* \*\*Validação de entrada:\*\* Impede salvar ou abrir leitura com nome de obra vazio ou número de capítulo inválido (menor que `1`), exibindo mensagem de feedback.

\* \*\*Persistência local com `SharedPreferences`:\*\* Mantém a obra ativa e o progresso de cada título mesmo após fechar e reabrir o aplicativo.

\* \*\*Interface em tema escuro/cinza opaco:\*\* Paleta em tons de cinza escuro pensada para reduzir o cansaço visual.



\## Tecnologias utilizadas



\* \*\*Linguagem:\*\* Kotlin

\* \*\*Interface:\*\* Jetpack Compose (Material 3)

\* \*\*Persistência local:\*\* `SharedPreferences`

\* \*\*IDE:\*\* Android Studio



\## Como executar o projeto



1\. Abra o \*\*Android Studio\*\*.

2\. Selecione \*\*Open\*\* e escolha a pasta raiz do projeto.

3\. Aguarde a sincronização do \*\*Gradle\*\* terminar.

4\. Conecte um dispositivo físico ou inicie um emulador Android e clique em \*\*Run (▶)\*\*.



> \*\*Observação:\*\* O projeto foi criado inicialmente sob a estrutura/pacote `com.example.capatual` e evoluiu para o fluxo atual de acompanhamento de capítulos.



\## Repositório



\* \*\*Código-fonte:\*\* \[Acesse o repositório no GitHub](https://github.com/nagaata)



\## Autor



João Vitor Ferreira Dias, Curso: Tecnologia em Análise e Desenvolvimento de Sistemas (Fundamentos de Desenvolvimento Mobile).


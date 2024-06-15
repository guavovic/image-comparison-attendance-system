# Facial Recognition Login System

## Descrição
Este projeto é um sistema de login que utiliza reconhecimento facial para autenticar usuários. Desenvolvido em Java, ele captura e analisa imagens faciais para verificar a identidade dos usuários.

## Funcionalidades
- Captura de imagem facial via webcam.
- Processamento e análise de imagens faciais.
- Autenticação de usuários baseada em reconhecimento facial.
- Interface amigável e intuitiva.

## Instalação

### Pré-requisitos
- Java Development Kit (JDK) 8 ou superior
- Apache Maven
- OpenCV (biblioteca de visão computacional)

### Passos
1. Clone o repositório:
    ```sh
    git clone https://github.com/guavovic/facial-recognition-login-system.git
    ```
2. Navegue até o diretório do projeto:
    ```sh
    cd facial-recognition-login-system
    ```
3. Instale as dependências do projeto usando Maven:
    ```sh
    mvn install
    ```
4. Certifique-se de que o OpenCV está configurado corretamente em seu sistema.

5. Compile e execute o projeto:
    ```sh
    mvn compile
    mvn exec:java -Dexec.mainClass="com.seuusuario.Main"
    ```

## Uso
1. Abra o aplicativo.
2. Aponte a câmera para o seu rosto.
3. O sistema irá capturar a imagem facial e tentar autenticar o usuário.

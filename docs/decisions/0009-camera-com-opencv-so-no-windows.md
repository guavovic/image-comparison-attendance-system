# 9. Câmera com OpenCV, só no Windows

Data: 01/10/2026

Status: Aceito

## Contexto

O ponto era batido escolhendo uma foto em arquivo, o que só serve para testar. Bater o ponto de verdade pede tirar a foto na hora, com a câmera. O Java não tem acesso a câmera embutido, então precisa de uma biblioteca.

## Opções consideradas

- **webcam-capture:** pequena (0,4 MB) e simples, mas a última versão é de 2018 e usa um componente nativo antigo, com risco de não funcionar no Java 25.
- **ffmpeg instalado na máquina:** leve, mas obriga quem usa o programa a instalar outra coisa.
- **OpenCV** (pacote `org.openpnp:opencv`): mantido, com as bibliotecas nativas dentro do `.jar`. O pacote traz os binários de Windows, Mac e Linux e pesa 110 MB.

## Decisão

OpenCV, só para Windows 64 bits.

- O `.jar` leva só `opencv_java490.dll` de Windows 64 bits. Os binários de Linux, Mac e Windows 32 bits são excluídos no empacotamento. O `.jar` passou de 13 MB para 33 MB.
- Fora do Windows, o diálogo da câmera mostra "A câmera só funciona no Windows." O resto do programa continua funcionando, com a escolha de arquivo.
- A câmera é acessada pelo DirectShow. Pelo Media Foundation (o padrão do OpenCV), abrir uma câmera levou entre 18 e 22 segundos nos testes, contra menos de 1 segundo pelo DirectShow.
- O Media Foundation também lista o celular quando ele está ligado ao Windows como câmera ("Câmera conectada"), e abrir esse índice liga a câmera do celular. O DirectShow também tem essa câmera virtual na lista, junto de outras (OBS Virtual Camera, Meta Quest), e abrir uma delas só para espiar produz o mesmo efeito. Por isso o programa **não sonda índices**.
- O diálogo lista as câmeras **pelo nome**, lidos do próprio DirectShow (um script do PowerShell chama a enumeração do Windows, que só lê os nomes e não abre nenhuma câmera, em cerca de meio segundo). As virtuais (nome com "virtual" ou "quest") ficam escondidas, e só a câmera escolhida é aberta. O índice de cada nome na lista é o mesmo que o OpenCV usa no DirectShow, o que foi conferido pedindo 1080p ao índice da C920. Se a primeira câmera não abrir, o diálogo passa sozinho para a próxima. A prévia é lida numa thread própria, para a tela não travar enquanto a câmera abre.
- O quadro é recortado em **quadrado, pelo centro**, e a prévia mostra um oval como guia. As fotos cadastradas são quadradas e a comparação reduz tudo para 100 x 100, então um quadro 16:9 seria esticado e nunca ficaria parecido. O recorte e o oval são os mesmos no ponto e no cadastro.
- **Bater ponto** passa a abrir a câmera, e **Usar uma foto** continua escolhendo um arquivo, que é o que os testes e os exemplos usam. No cadastro e na edição de funcionário, **Tirar foto** junta a foto da câmera às demais.
- `AttendanceService.punch` aceita uma imagem em memória (a foto da câmera não passa por arquivo). O aviso de foto não reconhecida diz se veio "da câmera" ou de qual arquivo.

## Consequências

- Dois quadros seguidos da mesma cena, numa câmera de notebook, tiraram nota 0,96, acima do limite de 0,91 mas com folga pequena. A comparação é pixel a pixel e depende de luz, distância e pose parecidas entre o cadastro e a hora do ponto. Com uso real, o limite pode precisar de ajuste, e o oval ajuda a manter o enquadramento.
- Nos testes, a câmera embutida (`ACER HD User Facing`) não abre pelo DirectShow, por um motivo que não foi descoberto, e o diálogo pula para a webcam externa (`HD Pro Webcam C920`), que funciona em 640x480, 720p e 1080p. Quem usar só a câmera embutida pode não conseguir tirar foto por ela.
- Os testes não carregam a biblioteca nativa: a conversão do quadro para imagem é Java puro (`Frames`) e é a parte testada. A captura em si é conferida com uma câmera de verdade.
- O CI roda no Linux, onde a câmera não existe, e por isso não exercita a captura.

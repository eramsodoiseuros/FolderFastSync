# Comunicação por Computador

Elaboração do serviço -> FolderFastSync: Sincronização rápida de pastas em ambientes serverless

Este trabalho surgiu no âbito da Unidade Curricular de Comunicações por computador e tem como objetivo implementar uma aplicação de sincronização rápida de pastas sem necessitar de servidores nem de conetividade Internet, designada por FolderFastSync (FFSync).

A aplicação utiliza como parâmetros quer a pasta a sincronizar quer o sistema parceiro com quem se vai sincronizar.
A aplicação corre em permanência dois protocolos: um de monitorização simples em HTTP sobre TCP e outro, a desenvolver de raiz para a sincronização de ficheiros sobre UDP.

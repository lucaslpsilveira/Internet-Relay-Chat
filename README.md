# Objetivo
O objetivo geral do trabalho é desenvolver uma aplicação usando sockets UDP e/ou TCP para
prover um serviço de bate-papo simplificado inspirado no protocolo IRC (Internet Relay
Chat). Os objetivos específicos incluem:
  compreender de maneira prática o mecanismo de comunicação por sockets;
  desenvolver uma aplicação distribuída usando o modelo cliente/servidor;
  implementar comunicação entre os processos usando sockets UDP e/ou TCP.

# Descrição
O serviço de bate-papo deverá ser implementado utilizando o modelo cliente/servidor. Um
programa servidor irá gerenciar canais de bate-papo e permitir que usuários se comuniquem
através desses canais, de forma similar ao serviço IRC (Internet Relay Chat). Assim, toda a
comunicação entre usuários deverá passar pelo servidor. Os usuários se conectam ao servidor
através de um programa cliente. Esse programa deve ler e interpretar os comandos digitados
pelo usuário e enviar uma mensagem correspondente ao servidor.
A lista completa de comandos disponíveis para o usuário e mensagens enviadas ao servidor
está descrita no ANEXO I. O programa cliente também deve receber mensagens enviadas
pelo servidor e mostrá-las ao usuário.
Sugestão: para facilitar o desenvolvimento, inicialmente a lista de canais pode ser criada
estaticamente no servidor (criar pelo menos 3 canais de bate-papo para testes).

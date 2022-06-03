# **FIGBOT** #

## **Indice** ##

- [Resumen](#resumen)
- [Requisitos](#requisitos)
- [Información sobre la ejecución](#información-sobre-la-ejecución)
    - [Ejecución con jar](#ejecucion-con-jar)
    - [Ejecución con gradle](#ejecucion-con-gradle)    
- [Integrantes del Grupo](#integrantes-del-grupo)

## **Resumen** ##

***Figbot*** se trata de un bot inteligente para la ayuda a la moderación en los canales
de *Twitch*. Está formado por un sistema multiagente que analiza los mensajes del chat
de modo que cuando se encuentran mensajes ofensivos el propio bot bloqueará a los usuarios 
ofensivos. Cuando no esté seguro de la acción a tomar expulsando temporalmente al usuario. 
El bot también dispone de comandos propios que pueden ser listados usando `f!help`.

## **Requisitos** ##

Será necesario un canal de <https://twitch.tv> donde debemos dar permisos de moderación
al usuario *@figb0t*.

Para la ejecución del proyecto será necesaria la versión 7.4 de gradle, en caso de no
disponer de gradle, es posible descargar el jar desde el propio repositorio.

## **Información sobre la ejecución** ##

### Ejecución con jar ###

Si se escoge la opcion del jar se debe usar `javaw -jar figbot.jar` desde el directorio donde
se encuentra descargado el proyecto.

### Ejecución con gradle ###

Debemos usar el comando `gradle build` para compilar el proyecto, tardará 
aproximadamente 30 segundos, posteriormente se debe ejecutar con `gradle run` que
generará una ventana (tras 6 segundos) donde introduciremos el nombre de nuestro canal de *Twitch*
y la zona horaria donde nos encontramos. 

Una vez pulsemos el boton *accept* se cambiará a una nueva ventana donde se irán mostrando 
los eventos de moderación que ocurren en el canal provenientes tanto de los moderadores del canal como de 
*figb0t*.

## **Integrantes del grupo** ##

Álvaro Alonso Miguel        190300 <br>
Idir Carlos Aliane Crespo   190384 <br>
Rafael Alonso Sirera        190182 <br>
Raúl Casamayor Navas        190243 <br>
# Avisi Tech Lab

Python (GUI): Gebruiker stuurt een foto in van het gewenste biertje. Het image classification model (in deze repository als stub) uit
de [vorige video](https://www.youtube.com/watch?v=Yipj2kJJzQw) bepaalt welk biertje op de foto staat. Stuurt door welk biertje gebrouwen dient te worden.

Kotlin (Inventory): Ontvangt welk biertje gebrouwen moet worden en zorgt ervoor dat de vereiste ingrediënten op voorraad komen. Doet een call naar een externe service (van de
leverancier, stub). Doorgeven aan een andere Kotlin service dat ingrediënten beschikbaar zijn.

Kotlin (Brewery): Ontvangt ingrediënten en het bier en brouwt het.

Python (GUI): Ontvangt een bevestiging van brouwen en levert het bier.

## Bouwen en starten

* Bouw de Kotlin services met Gradle door `./gradlew build` in de mappen "brewery" en "inventory" te draaien.
* Bouw de Docker images met `docker compose build`.
* Start RabbitMQ en de Docker images met `docker compose up -d`.
* Installeer de Python dependencies in de map "gui" met `pip install -r requirements.txt`. Python 3.8 wordt aangeraden omdat daarmee de applicatie is gemaakt.
* Start de applicatie met `export RABBITMQ_HOST=localhost && export RABBITMQ_USERNAME=beer && export RABBITMQ_PASSWORD=brewing && python -m app.main`

## De applicatie gebruiken

* Zorg dat je de logs van alle services kunt zien.
  * Voor de Kotlin services kun je dat doen met `docker compose logs inventory brewery -f`.
  * De logging van de Python applicatie is te zien in de terminal waar je die gestart hebt.
* Upload een foto in de GUI die door Python is gestart.
* In de logging zie je welke service waarmee bezig is en welke berichten ontvangen/verstuurd worden. Het resultaat van het brouwproces zie je uiteindelijk in de logging van Python!

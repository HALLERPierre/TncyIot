# Rapport de projet AMIO

# Réalisation d&#39;une application Android exploitant des données IoT


Nom des élèves : Pierre Haller et Florian KROMER

Élèves Ingénieur en 3ème année à  Télécom Nancy


# Cahier des charges

## Objectifs

L&#39;objectif de ce mini-projet est de réaliser une application Android exploitant des données issues d&#39;un réseau de capteurs et exposées à travers un web service (IoTLab de TELECOM Nancy). Le but est de détecter les lumières laissées actives dans les bureaux en soirée.

L&#39;application doit permettre les actions suivantes:

- Lister dans l&#39;activité principale les capteurs actifs et les valeurs de luminosité qu&#39;ils relèvent, en mettant en évidence ceux qui indiquent la présence d&#39;une lumière active
- Emettre une notification si une nouvelle lumière vient d&#39;être allumée en semaine entre 19h et 23h, en spécifiant le capteur impliqué
- Envoyer un email si cet événement survient le week-end entre 19h et 23h ou en semaine entre 23h et 6h.
- Permettre la configuration des plages horaires et de l&#39;adresse email dans un menu dédié

De plus, l&#39;état des capteurs seront récupérées dans un **Service** asynchrone (**AsyncTask)** qui exécutera une tache périodique ( **TimerTask** ). Celle-ci aura comme but la consultation d&#39;un web service avec **HttpURLConnection** et la lecture des réponses avec **JsonReader**. La persistance des données sera faite avec **SharedPreferences** et la création d&#39;un **BroadcastReceiver** pour gérer les événements système sera necessaire. Enfin il faudra éditer le   **Manifest** pour déclarer des permissions et des **intent-filter** , et l&#39;envoi de **Notification**.

## Technologies

Android studio : (v2.2.3) IDE, minSDKversion : 16, targetSDKversion : 21

Graddle : gestionnaire dépendances

Gson : (v2.2) bibliothèque permettant une manipulation du JSON plus souple que l&#39;api de base. Permet également de caster une chaine json en son objet correspondant.

JUnit : (v4.12) biblitotheque permettant l&#39;organisation et les assertions des tests unitaires.

JavaFaker : (v0.12) bibliothèque permettant de générer divers type de données, utile pour les tests unitaires.

L&#39;application a été testé sur un Xiaomi redme note 3 (android 6.0) et un nexus 4 (android 7)

# Réalisation

## Tests unitaires

Nous avons créé un package différent de celui des sources qui est uniquement destiné aux tests. Nous avons utilisé JUnit

Pour s&#39;assurer de la bonne communication entre notre TimerTask et le web service, nous avons mis en place différents tests unitaires. L&#39;organisation générale du code nous a permis facilement de tester chaque fonctionnalité indépendamment du reste. Par l&#39;exemple nous avons pu injecter un « faux » contexte propre aux les tests, de même nous pouvons injecter l&#39;url à appeler dans notre TimerTask à la volée, enfin le callback appeler à la fin de l&#39;execution de la tache périodique nous a permis de valider l&#39;assertion.

Dans le code ci-dessous nous testons si l&#39;appel à l&#39;API retourne au moins 1 résultat.
```java

@Test
public void testRESTLightLast(){
    LightTimerTask timerTask = new LightTimerTask() {
        @Override
        public void myTimerTaskCallback(LightRecords lightsRecordsList) {
            _assertTrue_(&quot;/{experiment\_id}/{labels}/(last|first) contains at leats 1 results&quot;,lightsRecordsList.size()&gt;=1);
        }

        @Override
        public Context myTimerTaskContext() {
            return getContext();
        }

        @Override
        public String myTimerTaskUrl() {
            return &quot;http://iotlab.telecomnancy.eu/rest/data/1/light1/last&quot;;
        }
    };
    timerTask.run();

}
```
Nous avons également testé l&#39;autre point clé de l&#39;application à savoir : la détection de l&#39;allumage d&#39;une lumière. Pour se faire, nous avons généré de fausses données grâce à la bibliotheque Faker avec une valeur de lumen encadré entre 2 et 200.
```java
Light l = new Light(faker.date().between(new Date(2017,1,1), new Date(2017,1,24)).getTime(),&quot;label&quot;,faker.number().randomDouble(2,2,200), &quot;mote9&quot;,);
```
Ensuite nous ajoutons 2 pics de valeurs pour simuler l&#39;allumage des lampes. Enfin nous testons qu&#39;il y a bien eu 2 changements d&#39;état pour la lampe.

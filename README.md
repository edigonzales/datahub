# datahub

## Testrequests

```
curl -i -X GET --header "X-API-KEY:c0bb04eb-789b-4063-95ad-bd86a06c6aff" http://localhost:8080/protected/hello
```


Admin:
```
curl -i -X POST --header "X-API-KEY:c0bb04eb-789b-4063-95ad-bd86a06c6aff" -F 'organisation=W+H AG' http://localhost:8080/api/keys
```

```
curl -i -X POST --header "X-API-KEY:c0bb04eb-789b-4063-95ad-bd86a06c6aff" -F 'organisation=SWG' http://localhost:8080/api/keys
```

```
curl -i -X POST --header "X-API-KEY:c0bb04eb-789b-4063-95ad-bd86a06c6aff" -F 'organisation=Märki AG' http://localhost:8080/api/keys
```

Admin darf beliebige Operate schicken:

```
curl -i -X POST --header "X-API-KEY:c0bb04eb-789b-4063-95ad-bd86a06c6aff" -F 'file=@Grenchen_LKMap_wasser.xtf' -F 'theme=LKMAP_2015' -F 'operat=2546_was' http://localhost:8080/api/deliveries
```


Märki AG:
```
curl -i -X GET --header "X-API-KEY:ca20e14c-faa7-4920-b0a5-c5a44476d80c" http://localhost:8080/protected/hello
```

```
curl -i -X POST --header "X-API-KEY:ca20e14c-faa7-4920-b0a5-c5a44476d80c" -F 'file=@2471.xtf' -F 'theme=IPW_2020' -F 'operat=2471' http://localhost:8080/api/deliveries
```

```
curl -i -X POST --header "X-API-KEY:ca20e14c-faa7-4920-b0a5-c5a44476d80c" -F 'file=@2622_gep.xtf' -F 'theme=IPW_2020' -F 'operat=2622' http://localhost:8080/api/deliveries
```







SWG:
```
curl -i -X GET --header "X-API-KEY:ab539116-210a-4ebf-ab9f-ee8786957fb6" http://localhost:8080/protected/hello
```

```
curl -i -X POST --header "X-API-KEY:ab539116-210a-4ebf-ab9f-ee8786957fb6" -F 'file=@Grenchen_LKMap_wasser.xtf' -F 'theme=LKMAP_2015' -F 'operat=2546_was' http://localhost:8080/api/deliveries
```

```
curl -i -X POST --header "X-API-KEY:ab539116-210a-4ebf-ab9f-ee8786957fb6" -F 'file=@Grenchen_LKMap_wasser_error.xtf' -F 'theme=LKMAP_2015' -F 'operat=2546_was' http://localhost:8080/api/deliveries
```

W+H AG:
```
curl -i -X GET --header "X-API-KEY:fda5f71f-8875-4d4a-af0c-87d9ba2832b6" http://localhost:8080/protected/hello
```
```
curl -i -X POST --header "X-API-KEY:fda5f71f-8875-4d4a-af0c-87d9ba2832b6" http://localhost:8080/api/keys
```

```
curl -i -X DELETE --header "X-API-KEY:fda5f71f-8875-4d4a-af0c-87d9ba2832b6" http://localhost:8080/api/keys/e2b76dce-5160-429b-96ec-0c64ed7c5027
```
```
curl -i -X POST --header "X-API-KEY:fda5f71f-8875-4d4a-af0c-87d9ba2832b6" -F 'file=@Aeschi_LKMap_Wasser.xtf' -F 'theme=LKMAP_2015' -F 'operat=2511_was' http://localhost:8080/api/deliveries
```

```
curl -i -X GET --header "X-API-KEY:fda5f71f-8875-4d4a-af0c-87d9ba2832b6" http://localhost:8080/api/jobs/010a0db3-821a-41a2-8059-83d9aaa2258b
```

```
curl -i -X GET --header "X-API-KEY:fda5f71f-8875-4d4a-af0c-87d9ba2832b6" http://localhost:8080/api/logs/010a0db3-821a-41a2-8059-83d9aaa2258b
```


```
curl -i -X GET --header 'Accept: application/json' --header "X-API-KEY:f3f65344-c909-4615-a2ea-7a05f7e27fbf" http://localhost:8080/api/jobs/0ecc0cd7-204e-4380-961b-a4b198f13675
```
```
curl -i -X GET --header 'Accept: text/html' --header "X-API-KEY:f3f65344-c909-4615-a2ea-7a05f7e27fbf" http://localhost:8080/api/jobs/0ecc0cd7-204e-4380-961b-a4b198f13675
```



```
curl -i -X POST -F 'file=@2549.ch.so.arp.nutzungsplanung.kommunal.xtf' -F 'theme=NPLNF' -F 'operat=2549' -u 'bob:uncle' http://localhost:8080/api/deliveries
```


```
curl -i -X POST -F 'file=@2471_gep.xtf' -F 'theme=IPW_2020' -F 'operat=2471' -u 'lisa:aunt' http://localhost:8080/api/deliveries
```

```
curl -i -X POST -F 'file=@Aeschi_LKMap_Wasser.xtf' -F 'theme=LKMAP_2015' -F 'operat=2511_was' -u 'hans:cousin' http://localhost:8080/api/deliveries
```
```
curl -i -X GET -H 'Accept: text/html' -u 'hans:cousin' http://localhost:8080/api/jobs/8bd96c37-65f6-45cb-a284-2cc714bc07c8
```
```
curl -i -X GET -H 'Accept: application/json' -u 'hans:cousin' http://localhost:8080/api/jobs/8bd96c37-65f6-45cb-a284-2cc714bc07c8
```


```
curl -i -X GET -u 'hans:cousin' http://localhost:8080/api/jobs/8bd96c37-65f6-45cb-a284-2cc714bc07c8
```

```
curl -i -X POST -F 'files=@2549.ch.so.arp.nutzungsplanung.kommunal.xtf' -u 'bob:uncle' http://localhost:8080/api/jobs
```

```
curl -i -X GET http://localhost:8080/ping
```

## Konzeptionelle Fragen
- Ich glaube mir wäre lieber es gibt nur Lieferungen und keine Prüfung, die ich dann zu einer Lieferung machen kann. Dünkt mich einfacher umzusetzen, da es weniger Logik braucht.
- Was wird genau geprüft? Wenn sogar der Inhalt (die Koordinaten) geprüft werden sollen, wirds teuer.
- Was wird geantwortet, wenn File nicht validiert? Prozessiert mit jobrunr ist es. Aber nicht valide? Ah, ist ja heute schon so. Ich habe zwei Stati. Es muss ja via GET jobsId nachgefragt werden (resp. gibt dann schon E-Mail / RSS / GUI / whatever)
- Wenn man will, dass der z.B. Import aufgrund des Filenamens alles weiss (Dataset-Name), muss man das File entweder korrekt benannt schicken (und das prüfen), oder beim wegkopieren umbenennen (muss in der DB stehen). Andere Varianten?
- Die Datei resp. das Operate wird vor der Prüfung nie überschrieben. Erst nach Validierung wird es in einen Thema-Ordner kopiert und überschreibt jeweils das ältere validierte Operat. Ist einfacher umzusetzen und auch (?) für die nachfolgenden Prozesse (wobei das nicht pauschalisiert werden kann, oder?). Aber wenn wir einfach aktuelle AV- oder was auch immer Daten wollen, ist überschreiben i.O.


## Autorisierung

- Wo hänge ich die ein in Spring Boot? Wie setze ich es um?
  * Filter? 
  * Normales Businesslogik im Service?
  * AccessDeniedHandler bringt mir nur was, wenn man Rollen hat? (hasRole). Woher soll sonst Spring Boot wissen, dass "bob" nicht autorisiert ist?



## Technische Fragen
- if "https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/X-Forwarded-Proto" == http THEN revoke key
- Um normale Validierungsjobs von Deliverjobs zu unterscheiden, einfach joinen, die reinen Validierungsjobs gibt es nicht in Deliveryjobs. Vielleicht so?
- Wie Jobs-Übersicht? Als erster Wurf "api/v1/jobs/<thema>/<operat>" etc. damit ich keine Bedienelemente brauche. Das Ganze sieht man nur authorisiert. man könnte alle gequeten Jobs zeigen, aber anonymisiert, wenn es nicht sein job ist. So könnte man sogar deleten (von gequeten) Jobs machen.
- Hochgeladene Dateien nicht zwischenspeichern (falls S3).
- Cleaner braucht es trotzdem? (an async denken resp. besser machen?)
- File too large exception? 413 status code? Sauberes API error handling (bis jetzt nur halbgar) -> https://www.toptal.com/java/spring-boot-rest-api-error-handling 
- @Andi: Sinnvolles Herstellen der DDL für GDI? Grants müssen noch gesetzt werden. 
- ...

## Datenmodell

- Es gibt offensichtliches, das fehlt.
- Nicht jede Organisation braucht einen Benutzer? -> Kann zwar nicht liefern, da immer ein Benutzer liefert. 
- Muss jedem Operat eine Organisation zugewiesen sein? Auch nicht wirklich. Vielleicht weiss man ja noch nicht wer liefert.


## Entwicklung

Datenbank starten (in dev-Verzeichnis):
```
docker-compose up
```

## Tests

### Token
- neues token:
  * falls org nicht vorhanden, darf es nicht funktionieren (bereits autho  nicht möglich).
  * 

- datümer bei den Keys


**API-Key-Version**
```
java -jar /Users/stefan/Downloads/ili2pg-4.9.1.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --defaultSrsCode 2056 --createGeomIdx  --createFk --createFkIdx --createEnumTabs --createMetaInfo --nameByTopic --strokeArcs --createUnique --createNumChecks --createTextChecks --createDateTimeChecks --createImportTabs --createUnique --dbschema agi_datahub_v2 --models "SO_AGI_Datahub_20240301" --modeldir "https://models.geo.admin.ch;ili/" --schemaimport
```

```
java -jar /Users/stefan/Downloads/ili2pg-4.9.1.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --dbschema agi_datahub_v2 --models "SO_AGI_Datahub_20240301" --modeldir "https://models.geo.admin.ch;ili/" --export datahub_key.xtf
```

```
java -jar /Users/stefan/Downloads/ili2pg-4.9.1.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --defaultSrsCode 2056 --createGeomIdx  --createFk --createFkIdx --createEnumTabs --createMetaInfo --nameByTopic --strokeArcs --createUnique --createNumChecks --createTextChecks --createDateTimeChecks --createImportTabs --createUnique --dbschema agi_datahub_v2 --models "SO_AGI_Datahub_20240301" --modeldir "https://models.geo.admin.ch;ili/" --doSchemaImport --import datahub_key.xtf
```

ili2pg 5.1.0

```
java -jar /Users/stefan/apps/ili2pg-5.1.0/ili2pg-5.1.0.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --defaultSrsCode 2056 --createGeomIdx  --createFk --createFkIdx --createEnumTabs --createMetaInfo --nameByTopic --strokeArcs --createUnique --createNumChecks --createTextChecks --createDateTimeChecks --createImportTabs --createUnique --dbschema agi_datahub_v1 --models "SO_AGI_Datahub_20240301" --modeldir "https://models.geo.admin.ch;ili/" --schemaimport
```

```
java -jar /Users/stefan/apps/ili2pg-5.1.0/ili2pg-5.1.0.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --defaultSrsCode 2056 --createGeomIdx  --createFk --createFkIdx --createEnumTabs --createMetaInfo --nameByTopic --strokeArcs --createUnique --createNumChecks --createTextChecks --createDateTimeChecks --createImportTabs --createUnique --dbschema agi_datahub_v1 --models "SO_AGI_Datahub_20240301" --modeldir "https://models.geo.admin.ch;ili/" --doSchemaImport --import datahub_key_with_data.xtf
```

```
java -jar /Users/stefan/apps/ili2pg-5.1.0/ili2pg-5.1.0.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --dbschema agi_datahub_v1 --models "SO_AGI_Datahub_20240301" --modeldir "https://models.geo.admin.ch;ili/" --export datahub_key.xtf
```

```
java -jar /Users/stefan/apps/ili2pg-5.1.0/ili2pg-5.1.0.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --dbschema agi_datahub_v1 --models "SO_AGI_Datahub_20240301" --modeldir "https://models.geo.admin.ch;ili/" --export datahub_key_with_data.xtf
```

**LDAP-Version**
Daten importieren:
```
java -jar /Users/stefan/Downloads/ili2pg-4.9.1.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --defaultSrsCode 2056 --createGeomIdx  --createFk --createFkIdx --createEnumTabs --createMetaInfo --nameByTopic --strokeArcs --createUnique --createNumChecks --createTextChecks --createDateTimeChecks --createImportTabs --dbschema agi_datahub_v1 --models "SO_AGI_Datahub_20240212" --modeldir "https://models.geo.admin.ch;ili/" --schemaimport
```

```
java -jar /Users/stefan/Downloads/ili2pg-4.9.1.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --defaultSrsCode 2056 --createGeomIdx  --createFk --createFkIdx --createEnumTabs --createMetaInfo --nameByTopic --strokeArcs --createUnique --createNumChecks --createTextChecks --createDateTimeChecks --createImportTabs --dbschema agi_datahub_v1 --models "SO_AGI_Datahub_20240212" --modeldir "https://models.geo.admin.ch;ili/" --doSchemaImport --import datahub.xtf
```

```
java -jar /Users/stefan/Downloads/ili2pg-4.9.1.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --dbschema agi_datahub_v1 --models "SO_AGI_Datahub_20240212" --modeldir "https://models.geo.admin.ch;ili/" --export datahub.xtf
```

## Jobrunr

Beide Jars manuell herunterladen.
```
java -cp jobrunr-6.3.4.jar:slf4j-api-2.0.12.jar org.jobrunr.storage.sql.common.DatabaseSqlMigrationFileProvider postgres agi_datahub_jobrunr_v1.
```
**ACHTUNG:** Der Punkt hinter dem Schema muss man setzen.

```
CREATE SCHEMA IF NOT EXISTS agi_datahub_jobrunr_v1;
```

## Cayenne

- Ein minimales datamap.map.xml muss vorhanden sein.

```
<?xml version="1.0" encoding="utf-8"?>
<data-map xmlns="http://cayenne.apache.org/schema/10/modelMap"
	 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	 xsi:schemaLocation="http://cayenne.apache.org/schema/10/modelMap https://cayenne.apache.org/schema/10/modelMap.xsd"
	 project-version="10">
	<property name="defaultPackage" value="ch.so.agi.datahub.cayenne"/>
</data-map>

```

```
./gradlew cdbimport
```

```
./gradlew cgen
```

- Probleme mit UUID-Datentyp -> String(36)

## Queries

```
SELECT 
    t.themeid,
    t.config,
    t.metaconfig,
    op.operatid,
    o.aname,
    u.userid,
    u.arole AS "role",
    u.isactive 
FROM 
    agi_datahub_v1.core_operat AS op 
    LEFT JOIN agi_datahub_v1.core_organisation AS o 
    ON o.t_id = op.organisation_r 
    LEFT JOIN agi_datahub_v1.core_organisation_user AS ou 
    ON o.t_id = ou.organisation_r 
    LEFT JOIN agi_datahub_v1.core_user AS u 
    ON u.t_id = ou.user_r 
    LEFT JOIN agi_datahub_v1.core_theme AS t 
    ON op.theme_r = t.t_id 
WHERE 
    u.userid = :userid
    AND 
    op.operatid = :operatid
    AND 
    t.themeid = :themeid
```

```
SELECT 
    *
FROM 
    agi_datahub_v1.core_theme AS t
    LEFT JOIN agi_datahub_v1.core_operat AS o 
    ON o.theme_r = t.t_id
    LEFT JOIN agi_datahub_v1.core_organisation AS org 
    ON o.organisation_r = org.t_id 
    LEFT JOIN agi_datahub_v1.core_organisation_user AS ou 
    ON org.t_id = ou.organisation_r 
    LEFT JOIN agi_datahub_v1.core_user AS u 
    ON ou.user_r = u.t_id 
```


## puml

```
@startuml
package "infogrips.ch" {    
    component [Checkservice] as checkservice
    component [Ablage] as ablage
}

package "Geodateninfrastruktur" {    
    database [Edit-DB] as editdb
}

checkservice --> ablage : isValid


actor "Datenlieferanten\n(wöchentlich)" as deliveryman

deliveryman --> checkservice : ftp und html form
editdb <-- ablage : ftp

legend right
  infogrips.ch:
  Login: "MOCHECKSO"
  Passwort: <E-Mail-Adresse>
endlegend

@enduml
```

```
@startuml

package "Geodateninfrastruktur" {  
    component [Checkservice] as checkservice
    component [Ablage] as ablage  
    database [Edit-DB] as editdb
}

checkservice --> ablage : isValid


actor "Datenlieferanten\n(wöchentlich)" as deliveryman

deliveryman --> checkservice : ????
editdb <-- ablage

legend right
  Anforderungen:
  - "ftp put"- resp. "curl post"-Niveau
  - IAM: Self registration / Passwort ändern durch Benutzer
  - Zukunftsfähige Lösung / Passt in die (zukünftige) kantonale Infrastruktur
endlegend

@enduml
```
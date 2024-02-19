# datahub

## Testrequests
```
curl -i -X POST -F 'file=@2549.ch.so.arp.nutzungsplanung.kommunal.xtf' -F 'theme=NPLNF' -F 'operat=2549' -u 'bob:uncle' http://localhost:8080/api/v1/deliveries
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

## Autorisierung

- Wo hänge ich die ein in Spring Boot? Wie setze ich es um?
  * Filter? 
  * Normales Businesslogik im Service?
  * AccessDeniedHandler bringt mir nur was, wenn man Rollen hat? (hasRole). Woher soll sonst Spring Boot wissen, dass "bob" nicht autorisiert ist?



## Technische Fragen
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
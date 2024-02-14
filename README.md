# datahub

## Testrequests
```
curl -i -X POST -F 'files=@2549.ch.so.arp.nutzungsplanung.kommunal.xtf' -u 'bob:uncle' http://localhost:8080/api/jobs

```

## Konzeptionelle Fragen
- Ich glaube mir wäre lieber es gibt nur Lieferungen und keine Prüfung, die ich dann zu einer Lieferung machen kann. Dünkt mich einfacher umzusetzen, da es weniger Logik braucht.
- Was wird genau geprüft? Wenn sogar der Inhalt (die Koordinaten) geprüft werden sollen, wirds teuer.

## Autorisierung

- Wo hänge ich die ein in Spring Boot? Wie setze ich es um?
  * Filter? 
  * Normales Businesslogik im Service?
  * AccessDeniedHandler bringt mir nur was, wenn man Rollen hat? (hasRole). Woher soll sonst Spring Boot wissen, dass "bob" nicht autorisiert ist?



## Technische Fragen
- Hochgeladene Dateien nicht zwischenspeichern (falls S3).
- Cleaner braucht es trotzdem? (an async denken resp. besser machen?)
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
java -jar /Users/stefan/Downloads/ili2pg-4.9.1.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --dbschema agi_datahub_v1 --models "SO_AGI_Datahub_20240212" --modeldir "https://models.geo.admin.ch;ili/" --export datahub.xtf
```


# datahub

```
curl -i -X POST -F 'files=@2549.ch.so.arp.nutzungsplanung.kommunal.xtf' -u 'bob:uncle' http://localhost:8080/api/jobs

```

## Entwicklung

Datenbank starten (in dev-Verzeichnis):
```
docker-compose up
```

Daten importieren:
```
java -jar /Users/stefan/Downloads/ili2pg-4.9.1.jar --dbhost localhost --dbport 54321 --dbdatabase edit --dbusr postgres --dbpwd secret --defaultSrsCode 2056 --createGeomIdx  --createFk --createFkIdx --createEnumTabs --createMetaInfo --nameByTopic --strokeArcs --createUnique --createNumChecks --createTextChecks --createDateTimeChecks --createImportTabs --dbschema agi_datahub_v1 --models "SO_AGI_Datahub_20240212" --modeldir "https://models.geo.admin.ch;ili/" --schemaimport
```
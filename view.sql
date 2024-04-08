CREATE OR REPLACE VIEW agi_datahub_log_v1.XXXXXXXXXX
WITH queue_position AS 
(
    SELECT 
        j.id, 
        createdat,
        ROW_NUMBER() OVER (ORDER BY createdat) AS queueposition
    FROM 
        agi_datahub_jobrunr_v1.jobrunr_jobs AS j
    WHERE 
        j.state = 'ENQUEUED'
)
SELECT 
    d.jobid,
    (j.createdat AT TIME ZONE 'UTC') AT TIME ZONE 'Europe/Zurich' AS createdat,
    (j.updatedat AT TIME ZONE 'UTC') AT TIME ZONE 'Europe/Zurich' AS updatedat,
    CASE 
        WHEN state = 'SUCCEEDED' THEN 'SUCCEEDED'
        ELSE state
    END as status, 
    queue_position.queueposition AS queueposition,
    op.aname AS operat,
    th.aname AS theme,
    org.aname AS organisation,
    CASE 
        WHEN isvalid IS TRUE THEN 'DONE' 
        WHEN isvalid IS FALSE THEN 'FAILED'
        ELSE NULL::TEXT
    END AS validationstatus
FROM 
    agi_datahub_jobrunr_v1.jobrunr_jobs AS j
    LEFT JOIN agi_datahub_log_v1.deliveries_delivery AS d 
    ON j.id = d.jobid 
    LEFT JOIN agi_datahub_config_v1.core_organisation AS org 
    ON org.aname = d.organisation 
    LEFT JOIN agi_datahub_config_v1.core_operat AS op
    ON d.operat = op.aname
    LEFT JOIN agi_datahub_config_v1.core_theme AS th 
    ON op.theme_r = th.t_id    
    LEFT JOIN queue_position 
    ON queue_position.id = j.id
WHERE 
    org.aname IS NOT NULL
;
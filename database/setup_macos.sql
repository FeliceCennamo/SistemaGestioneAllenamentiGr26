SET GLOBAL local_infile = 1;

LOAD DATA LOCAL INFILE 'INSERIRE PERCORSO ASSOLUTO CSV ATLETI' -- Usa gli slash / anche su Windows
INTO TABLE atleti
FIELDS TERMINATED BY ',' -- Il separatore dei campi (es. virgola o punto e virgola)
LINES TERMINATED BY '\n' -- Ritorno a capo (\n su Mac/Linux, \r\n su Windows)
IGNORE 1 LINES          -- Salta la prima riga se contiene i titoli delle colonne
(id,livello,cognome,disciplina,disciplinaPrevalente,mail,nome,password);


LOAD DATA LOCAL INFILE 'INSERIRE PERCORSO ASSOLUTO CSV ALLENATORI' -- Usa gli slash / anche su Windows
INTO TABLE allenatori
FIELDS TERMINATED BY ',' -- Il separatore dei campi (es. virgola o punto e virgola)
LINES TERMINATED BY '\n' -- Ritorno a capo (\n su Mac/Linux, \r\n su Windows)
IGNORE 1 LINES          -- Salta la prima riga se contiene i titoli delle colonne
(id,cognome,disciplinaPrevalente,mail,nome,password);


LOAD DATA LOCAL INFILE 'INSERIRE PERCORSO ASSOLUTO CSV ALLENATORE_ATLETA' -- Usa gli slash / anche su Windows
INTO TABLE allenatore_atleta
FIELDS TERMINATED BY ',' -- Il separatore dei campi (es. virgola o punto e virgola)
LINES TERMINATED BY '\n' -- Ritorno a capo (\n su Mac/Linux, \r\n su Windows)
IGNORE 1 LINES         -- Salta la prima riga se contiene i titoli delle colonne
(allenatore_id,atleta_id);

LOAD DATA LOCAL INFILE 'INSERIRE PERCORSO ASSOLUTO CSV ATLETA_OBIETTIVO' -- Usa gli slash / anche su Windows
INTO TABLE atleta_obiettivo
FIELDS TERMINATED BY ',' -- Il separatore dei campi (es. virgola o punto e virgola)
LINES TERMINATED BY '\n' -- Ritorno a capo (\n su Mac/Linux, \r\n su Windows)
IGNORE 1 LINES         -- Salta la prima riga se contiene i titoli delle colonne
(atleta_id,obiettivo);

LOAD DATA LOCAL INFILE 'INSERIRE PERCORSO ASSOLUTO CSV SESSIONEDIALLENAMENTO' -- Usa gli slash / anche su Windows
INTO TABLE sessionediallenamento
FIELDS TERMINATED BY ',' -- Il separatore dei campi (es. virgola o punto e virgola)
LINES TERMINATED BY '\n' -- Ritorno a capo (\n su Mac/Linux, \r\n su Windows)
IGNORE 1 LINES         -- Salta la prima riga se contiene i titoli delle colonne
(id,dataSvolgimento,stato,allenatore_id,atleta_id,descrizione,durata,titolo);

LOAD DATA LOCAL INFILE 'INSERIRE PERCORSO ASSOLUTO CSV ESERCIZIO' -- Usa gli slash / anche su Windows
INTO TABLE esercizio
FIELDS TERMINATED BY ',' -- Il separatore dei campi (es. virgola o punto e virgola)
LINES TERMINATED BY '\n' -- Ritorno a capo (\n su Mac/Linux, \r\n su Windows)
IGNORE 1 LINES         -- Salta la prima riga se contiene i titoli delle colonne
(id, nome, tipo, @var_ripetizioni, @var_durata, descrizione, @var_risultato,sessione_id)
SET ripetizioni = IF(@var_ripetizioni = 'NULL' OR @var_ripetizioni = '', NULL, @var_ripetizioni),
    durata      = IF(@var_durata = 'NULL'      OR @var_durata = '',      NULL, @var_durata),
    risultato   = IF(@var_risultato = 'NULL'   OR @var_risultato = '',   NULL, @var_risultato);
-- ROCKET (SoulUp) - script de criacao do banco Oracle
-- Execute por completo no schema Oracle da FIAP antes de subir a API.

CREATE TABLE usuario (
    id_usuario  NUMBER PRIMARY KEY,
    nome        VARCHAR2(100) NOT NULL,
    cpf         VARCHAR2(11)  NOT NULL UNIQUE,
    email       VARCHAR2(150) NOT NULL,
    senha       VARCHAR2(100),
    pontos      NUMBER DEFAULT 0,
    status      VARCHAR2(20) DEFAULT 'ATIVO'
);

CREATE TABLE missao (
    id_missao   NUMBER PRIMARY KEY,
    titulo      VARCHAR2(150) NOT NULL,
    descricao   VARCHAR2(255),
    pontos      NUMBER DEFAULT 0,
    categoria   VARCHAR2(30),
    status      VARCHAR2(20) DEFAULT 'DISPONIVEL',
    cpf_usuario VARCHAR2(11),
    CONSTRAINT fk_missao_usuario FOREIGN KEY (cpf_usuario) REFERENCES usuario (cpf)
);

CREATE TABLE voucher (
    id_voucher      NUMBER PRIMARY KEY,
    codigo          VARCHAR2(20) NOT NULL UNIQUE,
    cpf_usuario     VARCHAR2(11),
    tipo_beneficio  VARCHAR2(100),
    valor           NUMBER(10,2),
    status          VARCHAR2(20) DEFAULT 'DISPONIVEL',
    validade        DATE,
    CONSTRAINT fk_voucher_usuario FOREIGN KEY (cpf_usuario) REFERENCES usuario (cpf)
);

CREATE SEQUENCE seq_usuario START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_missao  START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seq_voucher START WITH 1 INCREMENT BY 1 NOCACHE;

-- ------------------------------------------------------------------
-- JA TEM AS TABELAS DA ENTREGA 3? Nao precisa recriar. Rode apenas:
--   ALTER TABLE voucher ADD CONSTRAINT uk_voucher_codigo UNIQUE (codigo);
-- (As senhas antigas, gravadas em texto puro, nao funcionam no login novo,
--  que usa hash. Cadastre os usuarios novamente ou apague-os.)
--
-- Para recomecar do zero:
-- DROP SEQUENCE seq_voucher; DROP SEQUENCE seq_missao; DROP SEQUENCE seq_usuario;
-- DROP TABLE voucher; DROP TABLE missao; DROP TABLE usuario;
-- ------------------------------------------------------------------

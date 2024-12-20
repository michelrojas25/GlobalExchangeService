CREATE TABLE IF NOT EXISTS TiposDeCambio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    moneda_origen VARCHAR(3) NOT NULL,
    moneda_destino VARCHAR(3) NOT NULL,
    tasa DECIMAL(10,4) NOT NULL,
    CONSTRAINT uk_monedas UNIQUE (moneda_origen, moneda_destino)
); 
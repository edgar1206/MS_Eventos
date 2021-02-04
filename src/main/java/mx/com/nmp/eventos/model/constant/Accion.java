package mx.com.nmp.eventos.model.constant;

public class Accion {

    public static final String [] name = {
            "Login",
            "CRUD tarjetas",
            "Token refresh",
            "Registro",
            "Campaña",
            "Promociones",
            "Boletas",
            "Movimientos",
            "Solicitar Pagos", //Solicitar Pagos - Solicitar PAgos
            "Registro tarjeta MS",
            "OpenPay read customer list from merchant - Lista vacia",
            "Associating the card to the customer",
            "Delete token"};

    public static final String [][] fases = {
            {"Autenticar","Usuario Monte","Initiate Auth"},
            {"Consulta tarjeta","Eliminar Tarjeta","Edición Tarjeta"},
            {"Autenticar"},
            {"Validar Datos","Solicitar Reinicio Contraseña","Registrar Nueva Contraseña","Solicitar Activación Token","Validar Medio Contacto","Solicitar Alta Cuenta","Token OAuth","Solicitar Reinicio Contraseña OAuth","Registrar Nueva Contraseña OAuth"}, //Registro tarjeta MS
            {"getCampaign"},
            {"getPromotions","savePromotion","deletePromotion"},
            {"Contratos Por Folio","Obtener Créditos","Desacarga Ticket"},
            {"Consulta","Descarga"},
            {"Realizar pago","Finalizar transacción"},
            {"Registro Tarjeta"},
            {"Registro Tarjeta"},
            {"Registro Tarjeta"},
            {"Autenticar"}
    };

    public static final Integer [][][] recurso = {
            {
                    { 12, 13 },
                    { 14, 15, 16 },
                    { 12, 13 }
            },
            {
                    { 10 },
                    { 11 },
                    { 10 }
            },
            {
                    { 3 },
            },
            {
                    { 4, 3 },
                    { 3 },
                    { 3 },
                    { 4 },
                    { 3, 5 },
                    { 4, 3 },
                    { 3 },
                    { 3 },
                    { 3 },
            },
            {
                    { 17 }
            },
            {
                    { 17 },
                    { 17 },
                    { 17 }
            },
            {
                    { 6 },
                    { 7 },
                    { 8 }
            },
            {
                    { 1 },
                    { 2 }
            },
            {
                    { 20 },
                    { 19 }
            },
            {
                    { 9 }
            },
            {
                    { 0 }
            },
            {
                    { 0 }
            },
            {
                    { 3 }
            }
    };

    public static final String [] recursos = {
            "OpenPay",
            "https://rsi.montepiedad.com.mx//NMP/OperacionPrendaria/OperacionesEnLinea/Transaccion.svc/v1/Movimientos/Detalle:8089",
            "Boletas",
            "https://rsi.montepiedad.com.mx//NMP/oauth/token:8089",
            "https://rsi.montepiedad.com.mx//NMP/GestionClientes/UsuariosMonte/v1/validarDatos:8089",
            "https://rsi.montepiedad.com.mx//NMP/GestionClientes/UsuariosMonte/v1/validarMedioContacto:8089",
            "https://rsi.montepiedad.com.mx//NMP/OperacionPrendaria/Partidas/v1/Folio:8089",
            "https://rsi.montepiedad.com.mx//NMP/OperacionPrendaria/Partidas/v1/Cliente:8089",
            "https://rsi.montepiedad.com.mx//NMP/OperacionPrendaria/EstadoCuenta/v1/PDF:8089",
            "https://1775-mimonte-fase2.mybluemix.net/mimonte/v1/tarjeta:443",
            "https://1775-mimonte-fase2.mybluemix.net/mimonte/v1/tarjetas/cliente/",
            "https://1775-mimonte-fase2.mybluemix.net/mimonte/v1/tarjeta/",
            "rsi.montepiedad.com.mx//8089//NMP/oauth/token:8089",
            "Cógnito",
            "https://iamdr.montepiedad.com.mx//NMP/GestionClientes/NivelCliente/v1?apiKey=",
            "https://rsi.montepiedad.com.mx//NMP/GestionClientes/NivelCliente/v1?apiKey=",
            "https://rsi.montepiedad.com.mx//NMP/GestionClientes/Cliente/v2/usuarioMonte:8089",
            "9243//d1ce82a01df14b9c8c1b1feb3ba3005d.us-east-1.aws.found.io/d1ce82a01df14b9c8c1b1feb3ba3005d.us-east-1",
            "https://rsi.montepiedad.com.mx//NMP/OperacionPrendaria/OperacionesEnLinea/Transaccion.svc/v1/FinalizarTransaccion",
            "https://rsi.montepiedad.com.mx//NMP/OperacionPrendaria/OperacionesEnLinea/Transaccion.svc/v1/Transaccion"
    };

    public static String [] codigoFase = {
            "AutenticarLogin",
            "Initiate Auth",
            "Usuario Monte",
            "Token OAuth",
            "Validar Datos",
            "Solicitar Activación Token",
            "Solicitar Alta Cuenta",
            "Validar Medio Contacto",
            "Registrar Nueva Contraseña",
            "Solicitar Reinicio Contraseña",
            "Registrar Nueva Contraseña OAuth",
            "Solicitar Reinicio Contraseña OAuth",
            "Registro Tarjeta MS",
            "Obtener Créditos",
            "Desacarga Ticket",
            "Contratos Por Folio",
            "AutenticarDeleteToken",
            "Consulta",
            "Descarga",
            "AutenticarRefreshToken",
            "getCampaign",
            "deletePromotion",
            "savePromotion",
            "getPromotions",
            "Edición Tarjeta",
            "Eliminar Tarjeta",
            "Consulta tarjeta",
            "Realizar pago",
            "Finalizar transacción",
            "Registro Tarjeta CC",
            "Registro Tarjeta OpenPay"
    };

    public static String [] accionFase = {
            "AutenticarLogin",
            "Registro Tarjeta MS",
            "AutenticarDeleteToken",
            "AutenticarRefreshToken",
            "Registro Tarjeta CC",
            "Registro Tarjeta OpenPay"
    };

}

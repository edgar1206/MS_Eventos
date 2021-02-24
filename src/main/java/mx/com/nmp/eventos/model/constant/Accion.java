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

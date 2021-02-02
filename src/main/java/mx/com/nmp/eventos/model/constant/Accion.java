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
            "Solicitar Pagos",
            "Registro tarjeta MS",
            "OpenPay read customer list from merchant - Lista vacia ",
            "Associating the card to the customer",
            "Delete token"};

    public static final String [][] fases = {
            {"Autenticar","Usuario Monte","Initiate Auth"},
            {"Consulta tarjeta","Eliminar Tarjeta","Edición Tarjeta"},
            {"Autenticar"},
            {"Validar Datos","Solicitar Reinicio Contraseña","Registrar Nueva Contraseña","Solicitar Activación Token","Validar Medio Contacto","Solicitar Alta Cuenta","Token OAuth","Solicitar Reinicio Contraseña OAuth","Registrar Nueva Contraseña OAuth","Registro Tarjeta"},
            {"getCampaign"},
            {"getPromotions","savePromotion","deletePromotion"},
            {"Contratos Por Folio","Obtener Créditos","Desacarga Ticket"},
            {"Consulta","Descarga"},
            {"Realizar pago"},
            {"Registro Tarjeta"},
            {"Registro Tarjeta"},
            {"Registro Tarjeta"},
            {"Autenticar"}
    };

}

package mx.com.nmp.eventos.model.constant;

public class Accion {

    public static final String [] name = {"Login","CRUD tarjetas","Token refresh","Registro","Campaña","Promociones","Boletas","Movimientos","Solicitar Pagos","Registro tarjeta MS","Solicitar PAgos","OpenPay read customer list from merchant - Lista vacia ","Associating the card to the customer","Delete token"};

    public static final String [][] fases = {
            {"Autenticar","Usuario Monte","Initiate Auth"},
            {"Validar Datos","Solicitar Reinicio Contraseña","Registrar Nueva Contraseña","Solicitar Activación Token","Validar Medio Contacto","Solicitar Alta Cuenta","Token OAuth","Solicitar Reinicio Contraseña OAuth","Registrar Nueva Contraseña OAuth","Registro Tarjeta"},
            {"Obtener créditos","Contratos por folio","Desacarga Ticket"},
            {"Consulta","Descarga"},
            {"getCampaign"},
            {"getPromotions","savePromotion","deletePromotion"},
            {"Consulta Tarjeta","Registro Tarjeta"},
            {"Realizar Pago","Finalizar Transaccion"}
    };

}

package dto;

import java.util.Map;

public class MessageDto {
    //Заголовок сообщения
    private String messageHeader;
    //ID вырабатываемого ключа
    //TODO: add keyID
    private String keyId;
    //ID отправителя
    private String senderId;
    //ID получателя
    private String recipientId;
    //Маршрут выработки ключа ключа, который был пройден
    private String keyRoutePassed;
    //Мршрут выработки ключа, который осталось пройти
    private String KeyRouteRemaining;
    //Мапа с вычисленными константами
    private Map<String, String> constantsMap;

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getKeyRoutePassed() {
        return keyRoutePassed;
    }

    public void setKeyRoutePassed(String keyRoutePassed) {
        this.keyRoutePassed = keyRoutePassed;
    }

    public String getKeyRouteRemaining() {
        return KeyRouteRemaining;
    }

    public void setKeyRouteRemaining(String keyRouteRemaining) {
        KeyRouteRemaining = keyRouteRemaining;
    }

    public String getMessageHeader() {
        return messageHeader;
    }

    public void setMessageHeader(String messageHeader) {
        this.messageHeader = messageHeader;
    }

    public Map<String, String> getConstantsMap() {
        return constantsMap;
    }

    public void setConstantsMap(Map<String, String> constantsMap) {
        this.constantsMap = constantsMap;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}

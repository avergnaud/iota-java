package hello;

import org.springframework.data.annotation.Id;

public class IotaInput {

    @Id
    public String id;

    private int keyIndex;

    public IotaInput() {}

    public IotaInput(int keyIndex) {
        this.keyIndex = keyIndex;
    }

    public int getKeyIndex() {
        return this.keyIndex;
    }
    public void setKeyIndex(int keyIndex) {
        this.keyIndex = keyIndex;
    }


    @Override
    public String toString() {
        return "IotaInput{" +
                "id='" + id + '\'' +
                ", keyIndex=" + keyIndex +
                '}';
    }
}

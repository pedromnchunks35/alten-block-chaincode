package chaincode.data;

import java.util.List;

public interface BiometricsDataInterface {
    public abstract String getUsername();

    public abstract List<List<Integer>> getTable();

    public abstract void setTable(List<List<Integer>> newTable);

    public abstract void setUsername(String username);

    public abstract boolean equals(BiometricsDataInterface anotherRecord);

    public abstract String hashKeccak256(String username);

    public abstract String tableToString();
}

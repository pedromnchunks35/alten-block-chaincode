package chaincode.data;

import java.util.List;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bouncycastle.util.encoders.Hex;

public class BiometricsData implements BiometricsDataInterface {
    private String username;
    private List<List<Integer>> table;

    /**
     * @param username, the username of the user
     * @param table,    the biometrics table data
     * @param hashed,   if the given username got hashed or not
     */
    public BiometricsData(String username, List<List<Integer>> table, Boolean hashed) {
        if (hashed) {
            this.username = username;
        } else {
            this.username = this.hashKeccak256(username);
        }
        this.table = table;
    }

    @Override
    protected BiometricsDataInterface clone() {
        return new BiometricsData(this.username, this.table, true);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "[username: " + this.getUsername() + ",table: " + this.tableToString() + "]";
    }

    @Override
    public boolean equals(BiometricsDataInterface anotherRecord) {
        if (anotherRecord.getUsername().equals(this.username) && anotherRecord.getTable().equals(this.table)) {
            return true;
        }
        return false;
    }

    @Override
    public List<List<Integer>> getTable() {
        return this.table;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    /**
     * @param username, a given string which represents the username of a user
     * @return A hexadecimal representation of the keccak hash algorithm
     */
    @Override
    public String hashKeccak256(String username) {
        Keccak.Digest256 digest256 = new Keccak.Digest256();
        byte[] hash = digest256.digest(username.getBytes());
        return Hex.toHexString(hash);
    }

    @Override
    public String tableToString() {
        String result = "[";
        for (int i = 0; i < this.table.size(); i++) {
            List<Integer> currentArray = this.table.get(i);
            result += "[";
            for (int j = 0; j < currentArray.size(); j++) {
                result += currentArray.get(j) + ",";
            }
            result = result.substring(0, result.length() - 1);
            result += "],";
        }
        result = result.substring(0, result.length() - 1);
        result += "]";
        return result;
    }

    @Override
    public void setTable(List<List<Integer>> newTable) {
        this.table = newTable;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

}

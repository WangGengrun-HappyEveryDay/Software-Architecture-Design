public class Main {
    public static void main(String[] args) {
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        try {
            FileSystem fileSystem = new FileSystem();

            String line;
            while ((line = reader.readLine()) != null) {
                fileSystem.execute(line, System.out);
            }
        } catch (java.io.IOException e) {
            // ignore
        }
    }
}



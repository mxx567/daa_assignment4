package algorithms;

public class LCS {
    public static int solve(String s1, String s2){
        int n = s1.length();
        int m = s2.length();
        int[][] table = new int[n+1][m+1];

        for(int i = 1; i < n + 1; i++){
            for(int j = 1; j < m + 1; j++){
                if(s1.charAt(i-1) == s1.charAt(j-1)){
                    table[i][j] = 1 + table[i-1][j-1];
                }
                else {
                    table[i][j] = Math.max(table[i-1][j], table[i][j-1]);
                }
            }
        }

        return table[n][m];
    }
}

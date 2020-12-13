package com.blockchain.simulator;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestIO {

    public static void copyCaseIntoTestFolder(final String caseName) throws IOException {
        final String sourseFolder = getSourceCaseFolder(caseName);
        final String targetFolder = getTargetCaseFolder(caseName);
        copyCaseFolder(sourseFolder, targetFolder);
    }

    public static String getRootDirectory() {
        return System.getProperty("user.dir");
    }

    /**
     * Copy source directory into destination directory recursively
     * @param srcFolder
     * @param destFolder
     * @throws IOException
     */
    public static void copyCaseFolder(final String srcFolder, final String destFolder) throws IOException {
        FileUtils.copyDirectory(new File(srcFolder), new File(destFolder));
    }

    public static String getSourceCaseFolder(final String caseFolderName) {
        Path filePath = Paths.get(getRootDirectory(), "src", "config", caseFolderName);
        return filePath.toString();
    }

    public static String getTargetCaseFolder(final String caseFolderName) {
        Path filePath = Paths.get(getRootDirectory(), "test_targets", caseFolderName);
        return filePath.toString();
    }

    public static String getPlayerStateTraceFileName(final String caseFolder, final int round) {
        final String roundText;
        if (round == -1) {
            roundText = "init";
        } else {
            roundText = Integer.toString(round);
        }
        Path filePath = Paths.get(caseFolder, "player_state", roundText + ".json");
        return filePath.toString();
    }

    public static String getDolevStrongOutputFileName(final String caseFolder) {
        Path filePath = Paths.get(caseFolder, "output.json");
        return filePath.toString();
    }

    public static String getStreamletProposalFileName(final String caseFolder, final int round) {
        Path filePath = Paths.get(caseFolder, "proposal_trace", round + ".json");
        return filePath.toString();
    }

    public static void deleteFolder(final String folderPath) throws IOException {
        FileUtils.deleteDirectory(new File(folderPath));
    }
}

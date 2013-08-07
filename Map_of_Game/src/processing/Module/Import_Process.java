/**Import_Process
 * 
 * Takes a valid filepath that the user has specified and turns that song into 
 * a processed list of information that would give a header
 * that contains full filepath for easy streaming
 * the processed data for choice of obstacles by the AI
 * other misc data to be determined from further investigation
 * into the AI.
 * 
 */

/**
 * Called by: Import_UI
 * Calls: Audio_process, AI_decision, 
 * 
 * Input of: 
 * filepath in an Intent
 * 
 * Output of:
 * a file in project folder that has all the information required
 * encoded within.
 * 
 * @author Shen Wang
 *
 */

package processing.Module;

public class Import_Process {
	
	
	/**Create Header:
	 * Creates a header information that is the full file path to the song then a new line separation
	 */
	
	/**Song Data:
	 * Calls Audio_process on the filepath given and the output 
	 * would be an array output.
	 * Then call the AI_deicsion which will then output the string
	 * to be written to the folder.
	 */
	
	
	/**Write Data:
	 * TODO Makes file within folder (that was created when application was first opened?)
	 * Name of file is the full song name without the extension .wav or .mp3 or w.e. file format we accept
	 * Save this filepath as "save_path"
	 * Writes the path data and the song info into the file together as a full set
	 * so that nothing will accidentally be overwritten.
	 */
	
	
	/**Clean Up:
	 * Cleans up all variables and dumps everything.
	 */

}

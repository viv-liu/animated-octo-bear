/**Play_stream
 * 
 * Buffers the audio for playing during the game.
 * Allows for playing and pausing of the audio whenever necessary
 * and always prepares the next section to be buffered well ahead
 * of needed to provide continued playback.
 * 
 * 
 */

/**
 * Called by: Play_process, Play_response?
 * Calls: AudioTrack?
 * 
 * Input of:
 * Intent with filepath
 * 
 * Output of:
 * A buffered audio from the filepath ready to play
 * played and paused on call from Play_response
 * 
 * 
 * @author Shen Wang
 *
 */

package play.module;

public class Play_stream {

}

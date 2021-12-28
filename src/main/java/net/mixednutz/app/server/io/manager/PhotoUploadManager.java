package net.mixednutz.app.server.io.manager;

import java.io.File;
import java.io.IOException;

import net.mixednutz.app.server.entity.User;
import net.mixednutz.app.server.io.domain.FileWrapper;
import net.mixednutz.app.server.io.domain.PersistableFile;

public interface PhotoUploadManager {
	
	public enum Size {
		
		ORIGINAL(),
		LARGE(),
		SMALL(),
		TINY(),
		AVATAR(),
		BOOK();
		
		final String size;
		
		private Size() {
			this.size = this.name().toLowerCase();
		}

		public String getSize() {
			return size;
		}
		
		public static Size getValue(String size) {
			return Size.valueOf(size.toUpperCase());
		}

	}
	
	/**
	 * Uploads a PersistableFile to the local photos directory and also the cloud.
	 * Use this method when you have a stream and a known content type.
	 * 
	 * @param accountId
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public String uploadFile(User user, PersistableFile file) throws IOException;
	
	public String uploadFile(User user, PersistableFile file, Size size) throws IOException;
	
	/**
	 * Uploads a file to the local photos directory and also the cloud.
	 * Use this method when you don't know the content type.
	 * 
	 * @param accountId
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public String uploadFile(User user, File file) throws IOException;
	
	public String uploadFile(User user, File file, Size size) throws IOException;
	
	/**
	 * Uploads a file to the local photos directory and also the cloud.
	 * Use this method when you don't know the content type.
	 * 
	 * @param accountId
	 * @param file
	 * @param renameToFilename if not null, rename file to this.
	 * @param replaceIfExisting replace files if already existing
	 * @return
	 * @throws IOException
	 */
	public String uploadFile(User user, File file, String renameToFilename, boolean replaceIfExisting) throws IOException;
	
	/**
	 * Downloads a PersistableFile.  If the file doesn't exist locally it will be fetched
	 * from the cloud.
	 * 
	 * @param account
	 * @param filename
	 * @param size
	 * @return
	 * @throws IOException 
	 */
	public FileWrapper downloadFile(User user, String filename, Size sizeName) throws IOException;
	
}

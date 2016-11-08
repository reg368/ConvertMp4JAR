import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;

public class BackgroundThread extends Thread {

	private JButton btSubmit;
	private InputInfoVO input;
	private JTextArea taResult;
	private JLabel exeInfo;

	private List<Map<String, String>> fileList = new ArrayList<Map<String, String>>();
	final private String convert = "-i ${fromPath} -c:v libx264 -preset ultrafast -qp 18 ${toPath}";
	final private Runtime rt = Runtime.getRuntime();

	BackgroundThread(JButton btSubmit, JTextArea taResult, InputInfoVO input,
			JLabel exeInfo) {
		this.btSubmit = btSubmit;
		this.input = input;
		this.taResult = taResult;
		this.exeInfo = exeInfo;
	}

	public void run() {
		
		URI  ffmpeg = null;
		this.btSubmit.setEnabled(false);
		
		Connection con = null;
		PreparedStatement pstmt;
		try {
						
			DriverManager
					.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
			con = DriverManager.getConnection("jdbc:sqlserver://"
					+ input.getUrl() + ";user=" + input.getId() + ";password="
					+ input.getPwd() + ";database=" + input.getDbName());
			taResult.append("連線成功\n");
			pstmt = con.prepareStatement("SELECT  * FROM UploadFile");
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {

				String ufid = rs.getString(1);
				// String type = rs.getString(2);
				String dir = rs.getString(5);

				String from = input.getPath() + "\\" + dir + "\\" + ufid + "."
						+ "flv";
				taResult.append("*Get DB data  : " + from + " \n");
				String to = input.getPath() + "\\" + dir + "\\" + ufid + ".mp4";
				// taResult.append("*Get DB data to : "+to+" \n");

				Map<String, String> pathMap = new HashMap<String, String>();
				pathMap.put("from", from);
				pathMap.put("to", to);
				fileList.add(pathMap);

			}
			con.commit();
			con.setAutoCommit(true);

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			taResult.append(ex.getMessage());
			if (con != null) {
				try {
					System.out.println("Transaction begin, start rollback");
					con.rollback();
				} catch (Exception eg) {
					System.out.println(eg.getMessage());
					taResult.append(eg.getMessage());
				}
			}
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (SQLException sqle) {
					// TODO Auto-generated catch block
					sqle.printStackTrace();
					taResult.append(sqle.getMessage());
				}
		}

		File ffmpegFile = new File(input.getFfmpeg());
		
		if (fileList != null && !fileList.isEmpty() && ffmpegFile.exists()) {

			for (Map<String, String> fileMap : fileList) {

				String from = fileMap.get("from");
				String to = fileMap.get("to");

				File oldVideo = new File(from);

				if (oldVideo.exists()) {
					try {

						String[] callCmd = new String[] { "cmd.exe", "/C", "" };
						String exeFfmpeg = input.getFfmpeg()
								+ " "
								+ convert.replace("${fromPath}", from).replace(
										"${toPath}", to);
						System.out.println("exeFfmpeg : " + exeFfmpeg);
						callCmd[2] = exeFfmpeg;
						taResult.append("***開始轉檔*** : " + from + "\n");
						Process process = rt.exec(callCmd);

						InputStream stderr = process.getErrorStream();
						InputStreamReader isr = new InputStreamReader(stderr);
						BufferedReader br = new BufferedReader(isr);
						String line = null;
						while ((line = br.readLine()) != null) {
							System.out.println(line);
						}
						taResult.append("*轉檔結束* : " + to + "\n");

					} catch (Exception eg) {
						System.out.println("轉檔exception : " + eg.getMessage()
								+ " from : " + from);
						taResult.append("轉檔exception : " + eg.getMessage()
								+ " from : " + from + " \n");
					}
				} else {
					taResult.append("檔案不存在 : " + from + "\n");
				}
			}
		} else {
			taResult.append("ffmpeg檔案不存在 或 影音檔不存在 \n");
		}

		this.btSubmit.setEnabled(true);
		this.exeInfo.setText("執行完成");
	}

//	private static URI getJarURI() throws URISyntaxException {
//		final ProtectionDomain domain;
//		final CodeSource source;
//		final URL url;
//		final URI uri;
//
//		domain = BackgroundThread.class.getProtectionDomain();
//		source = domain.getCodeSource();
//		url = source.getLocation();
//		uri = url.toURI();
//
//		return (uri);
//	}

//	private static URI getFile(final URI where, final String fileName)
//			throws ZipException, IOException {
//		final File location;
//		final URI fileURI;
//
//		location = new File(where);
//
//		// not in a JAR, just return the path on disk
//		if (location.isDirectory()) {
//			fileURI = URI.create(where.toString() + fileName);
//		} else {
//			final ZipFile zipFile;
//
//			zipFile = new ZipFile(location);
//
//			try {
//				fileURI = extract(zipFile, fileName);
//			} finally {
//				zipFile.close();
//			}
//		}
//
//		return (fileURI);
//	}
//
//	private static URI extract(final ZipFile zipFile, final String fileName)
//			throws IOException {
//		final File tempFile;
//		final ZipEntry entry;
//		final InputStream zipStream;
//		OutputStream fileStream;
//
//		tempFile = File.createTempFile(fileName,
//				Long.toString(System.currentTimeMillis()));
//		tempFile.deleteOnExit();
//		entry = zipFile.getEntry(fileName);
//
//		if (entry == null) {
//			throw new FileNotFoundException("cannot find file: " + fileName
//					+ " in archive: " + zipFile.getName());
//		}
//
//		zipStream = zipFile.getInputStream(entry);
//		fileStream = null;
//
//		try {
//			final byte[] buf;
//			int i;
//
//			fileStream = new FileOutputStream(tempFile);
//			buf = new byte[1024];
//			i = 0;
//
//			while ((i = zipStream.read(buf)) != -1) {
//				fileStream.write(buf, 0, i);
//			}
//		} finally {
//			close(zipStream);
//			close(fileStream);
//		}
//
//		return (tempFile.toURI());
//	}
//
//	private static void close(final Closeable stream) {
//		if (stream != null) {
//			try {
//				stream.close();
//			} catch (final IOException ex) {
//				ex.printStackTrace();
//			}
//		}
//	}
}

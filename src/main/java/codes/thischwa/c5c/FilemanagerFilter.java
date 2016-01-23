package codes.thischwa.c5c;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilemanagerFilter implements Filter {
	private static Logger logger = LoggerFactory.getLogger(FilemanagerFilter.class);
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info(String.format("*** %s sucessful initialized.", this.getClass().getName()));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		String path = req.getServletPath();
		if(!path.contains("filemanager.config.js") && !path.startsWith(Constants.REQUEST_PATH_TOIGNORE)) {
			InputStream in = FilemanagerFilter.class.getResourceAsStream(path);
			OutputStream out = null;
			try {
				if(in == null) {
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					logger.warn("Requested path not found: {}", path);
				} else {
					out = resp.getOutputStream(); // shouldn't be flushed, because of the filter-chain
					IOUtils.copy(in,out);
				}
			} catch (IOException e) {
				logger.warn("Error while reading requested resource: ", path, e);
			} finally {
				IOUtils.closeQuietly(out);
			}
		} else  {
			chain.doFilter(req, resp);
		}
	}
	
	@Override
	public void destroy() {		
	}
}

package com.wks.CalorieApp.Controllers;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.ImageSearcherFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.wks.CalorieApp.Models.Identifier;
import com.wks.CalorieApp.StatusCodes.IndexStatusCodes;
import com.wks.CalorieApp.Utils.Environment;

/*
 * - think of a better JSON writer impmentation.
 * - create an abstract class for index writing
 * - handle duplication??
 */

public class Identify extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "application/json";
	private static final String PARAMETER_SEPERATOR = "/";
	private static final String PARAM_DEFAULT_MAX_HITS = "default_max_hits";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doPost(req, resp);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String imagesDir = Environment.getImagesDirectory(getServletContext());
		String indexesDir = Environment
				.getIndexesDirectory(getServletContext());
		int defaultMaxHits = Integer.parseInt(getServletContext()
				.getInitParameter(PARAM_DEFAULT_MAX_HITS)) | 10;
		int maximumHits = defaultMaxHits;

		resp.setContentType(CONTENT_TYPE);
		PrintWriter out = resp.getWriter();

		String[] parameters = req.getPathInfo().split(PARAMETER_SEPERATOR);

		if (parameters.length < 2) {
			// TODO
			outputJSON(out, false, IndexStatusCodes.TOO_FEW_ARGS.getDescription());
			return;
		}
		if (parameters.length > 2) {
			// check that maximum number hits provided and value is int
			try {
				maximumHits = Integer.parseInt(parameters[2]);
			} catch (NumberFormatException nfe) {
				// TODO
				outputJSON(out, false,
						IndexStatusCodes.TOO_FEW_ARGS.getDescription());
				return;
			}
		}

		// check that file exists
		String fileURI = imagesDir + parameters[1];

		ImageSearcher searcher = ImageSearcherFactory
				.createAutoColorCorrelogramImageSearcher(maximumHits);
		Identifier srchr = new Identifier(searcher);

		try {
			String[] similarImages = srchr.findSimilarImages(fileURI,
					indexesDir, maximumHits);
			JSONArray similarImagesJSON = new JSONArray();
			for (String uri : similarImages)
				similarImagesJSON.add(uri);

			outputJSON(out, true, similarImagesJSON.toJSONString());
		} catch (IOException e) {
			// TODO create indexer codes
			outputJSON(out, false, IndexStatusCodes.IO_ERROR.getDescription());
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void outputJSON(PrintWriter out, boolean success, String message) {
		JSONObject json = new JSONObject();
		json.put("message", message);
		json.put("success", success);
		out.println(json);

	}

}

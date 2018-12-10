package com.application.resume;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/v1/resume")
public class ResumeService {
	Map<String, String> qaMap = null;
	Map<Character, Set<Character>> valueMap = new HashMap<Character, Set<Character>>();

	/**
	 * Initialing the qaMap which stores the response to questions asked by the client
	 * 
	 */
	Map<String, String> getInitMap() {
		qaMap = new HashMap<String, String>();
		qaMap.put("Ping", "OK");
		qaMap.put("Name", "Varun Bhat");
		qaMap.put("Years", "3+");
		qaMap.put("Email Address", "varun8969@gmail.com");
		qaMap.put("Phone", "423-429-2501");
		qaMap.put("Referrer", "LinkedIn");
		qaMap.put("Degree", "MS in Computer Engineering from Purdue University");
		qaMap.put("Resume", "https://www.dropbox.com/sh/r0oxyc8m2ciao7s/AACwopAKh_tefiPfV2EtkrB-a?dl=0");
		qaMap.put("Status", "Yes, Currently on H1B");
		qaMap.put("Source", "https://github.com/Varun91/EMXResumeTest.git");
		qaMap.put("Position", "Software Engineer");
		return qaMap;
	}


	@GET
	public Response getMsg(@Context Request request, @Context UriInfo uriInfo) {
		if (qaMap == null) {
			qaMap = getInitMap();
		}

		String query = uriInfo.getQueryParameters().getFirst("q");
		if (query.equals("Puzzle")) {
			String soln = solvePuzzle(uriInfo.getQueryParameters().getFirst("d"));
			return Response.status(200).entity(soln).build();
		}
		return Response.status(200).entity(qaMap.get(query)).build();

	}

	/**
	 * Solves the Puzzle
	 */
	private String solvePuzzle(String query) {
		System.out.println(query);
		BufferedReader bufReader = new BufferedReader(new StringReader(query));
		String line = null;
		char[][] mat;
		List<String> list = new ArrayList<String>();
		try {
			while ((line = bufReader.readLine()) != null) {
				list.add(line.trim());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		list.remove(0);
		int rows = list.get(0).length();
		int cols = list.get(0).length();
		mat = new char[rows][cols];
		String header = list.get(0);
		list.remove(0);
		mat = initMatrix(mat, list);
		mat = updateMatrix(mat, list);
		return matToString(mat, header);
	}
	/**
	 * Updates the matrix with new values got after executing DFS
	 * 
	 */
	private char[][] updateMatrix(char[][] mat, List<String> list) {
		for (int i = 0; i < mat[0].length; i++) {
			for (int j = 0; j < mat.length; j++) {
				if (i == j)
					mat[i][j] = '=';
				else if (mat[i][j] == '-' && i < j) {
					if (valueMap.containsKey(list.get(i).charAt(0))
							&& valueMap.get(list.get(i).charAt(0)).contains(list.get(j).charAt(0)))
						mat[i][j] = '>';
					else
						mat[i][j] = '<';
				} else if (mat[i][j] == '-' && i > j) {
					if (valueMap.containsKey(list.get(j).charAt(0))
							&& valueMap.get(list.get(j).charAt(0)).contains(list.get(i).charAt(0)))
						mat[i][j] = '<';
					else
						mat[i][j] = '>';
				}
			}
		}
		return mat;
	}
	/**
	 * Initializes the matrix with the initial values provided in the query
	 *
	 */

	private char[][] initMatrix(char[][] mat, List<String> list) {
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.get(i).length() - 1; j++) {
				if (mat[i][j] == 0)
					mat[i][j] = list.get(i).charAt(j + 1);
				if (mat[i][j] == '>') {
					mat[j][i] = '<';
					char rowIndex = list.get(i).charAt(0);
					if (valueMap.containsKey(rowIndex)) {
						valueMap.get(rowIndex).add(list.get(j).charAt(0));
					} else {
						Set<Character> set = new HashSet<Character>();
						set.add(list.get(j).charAt(0));
						valueMap.put(rowIndex, set);
					}

				} else if (mat[i][j] == '<') {
					mat[j][i] = '>';
					char rowIndex = list.get(j).charAt(0);
					if (valueMap.containsKey(rowIndex)) {
						valueMap.get(rowIndex).add(list.get(i).charAt(0));
					} else {
						Set<Character> set = new HashSet<Character>();
						set.add(list.get(i).charAt(0));
						valueMap.put(rowIndex, set);
					}
				}
			}
		}
		for (Character c : valueMap.keySet()) {
			updateValueMapRecur(c, valueMap.get(c));
		}
		return mat;
	}
	
	/**
	 * Uses DFS to update the ValueMap
	 * 
	 */

	private void updateValueMapRecur(Character ch, Set<Character> valueSet) {
		for (Character c : valueSet) {
			valueMap.get(ch).add(c);
			if (valueMap.containsKey(c)) {
				updateValueMapRecur(ch, valueMap.get(c));
			}

		}

	}
	
	/**
	 * Converts the matrix to string format
	 * 
	 */

	private String matToString(char[][] mat, String header) {
		StringBuilder s = new StringBuilder();
		s.append(" " + header + "\n");
		for (int i = 0; i < mat[0].length; i++) {
			s.append(header.charAt(i));
			for (int j = 0; j < mat.length; j++) {
				s.append(mat[i][j]);
			}
			s.append("\n");
		}
		return s.toString();
	}

}
package jrbn.programs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jrbn.actions.ExtractSentences;
import nl.vu.cs.ajira.Ajira;
import nl.vu.cs.ajira.actions.ActionConf;
import nl.vu.cs.ajira.actions.ActionFactory;
import nl.vu.cs.ajira.actions.ActionSequence;
import nl.vu.cs.ajira.actions.ReadFromFiles;
import nl.vu.cs.ajira.exceptions.ActionNotConfiguredException;
import nl.vu.cs.ajira.submissions.Job;
import nl.vu.cs.ajira.submissions.Submission;
import nl.vu.cs.ajira.utils.Consts;

public class Wiki_ExtractEntities {

	static final Logger log = LoggerFactory.getLogger(Wiki_ExtractEntities.class);

	private static Job createJob(String inputDir) throws ActionNotConfiguredException {
		Job job = new Job();
		ActionSequence actions = new ActionSequence();
		ActionConf action = ActionFactory.getActionConf(ReadFromFiles.class);
		action.setParamString(ReadFromFiles.S_PATH, inputDir);
		actions.add(action);

		action = ActionFactory.getActionConf(ExtractSentences.class);
		actions.add(action);

		job.setActions(actions);
		return job;
	}

	public static void main(String[] args) {
		// Analyse sentences for each page
		if (args.length < 1) {
			System.out.println("Usage: " + Wiki_ExtractEntities.class.getSimpleName() + " <input directory>");
			System.exit(1);
		}

		// Load in a global datastructure all facts to check

		// Start up the cluster
		Ajira ajira = new Ajira();
		try {
			ajira.startup();
		} catch (Throwable e) {
			log.error("Could not start up Ajira", e);
			System.exit(1);
		}

		// With this command we ensure that we submit the job only once
		if (ajira.amItheServer()) {
			// Configure the job and launch it!
			try {
				Job job = createJob(args[0]);
				Submission sub = ajira.waitForCompletion(job);
				sub.printStatistics();
				if (sub.getState().equals(Consts.STATE_FAILED)) {
					log.error("The job failed", sub.getException());
				}

			} catch (ActionNotConfiguredException e) {
				log.error("The job was not properly configured", e);
			} catch (Exception e) {
				log.error("Generic error", e);
			} finally {
				ajira.shutdown();
			}
		}
	}
}
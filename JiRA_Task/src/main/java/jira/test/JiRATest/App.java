package jira.test.JiRATest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.JiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

/**
 * Jira Task on GitHub
 *
 */
public class App {
		
	public static void main(String[] args) throws URISyntaxException {
		System.out.println("Starting the code ...");
		System.out.println("---------------------");
		
		final JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		
		/*... in production ...*/
		final URI jiraServerUri = new URI("http://localhost/");
		
		final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, "user", "pass");
		
		final Issue issue = restClient.getIssueClient().getIssue("issue-id").claim();
		System.out.println(issue);
		
		//restClient.getIssueClient().vote(issue.getVotesUri()).claim();
		restClient.getIssueClient().watch(issue.getWatchers().getSelf()).claim();
		
		// progressing the issue
		final Iterable<Transition> transitions = restClient.getIssueClient().getTransitions(issue.getTransitionsUri()).claim();
        final Transition startProgressTransition = getTransitionByName(transitions, "Reopen Issue");
        restClient.getIssueClient().transition(issue.getTransitionsUri(), new TransitionInput(startProgressTransition.getId())).claim();
		
        final Transition resolveIssueTransition = getTransitionByName(transitions, "Resolve Issue");
        Collection<FieldInput> fieldInputs = Arrays.asList(new FieldInput("resolution", "Incomplete"));
        final TransitionInput transitionInput = new TransitionInput(resolveIssueTransition.getId(), fieldInputs, Comment.valueOf("My comment"));
        restClient.getIssueClient().transition(issue.getTransitionsUri(), transitionInput).claim();
	   	
        //System.out.println(issue.getTransitionsUri());
		System.out.println("--------------------");
	   	System.out.println("... End of the code.");
	}

	private static Transition getTransitionByName(Iterable<Transition> transitions, String transitionName) 
	{
		// TODO Auto-generated method stub
		for (Transition transition : transitions) {
            if (transition.getName().equals(transitionName)) {
            	//System.out.println(transition.getId());
                return transition;
            }
        }
		return null;
	}
}

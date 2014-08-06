package eu.lod2.rsine.queryhandling.policies;

import org.springframework.stereotype.Component;

import eu.lod2.rsine.registrationservice.NotificationQuery;

@Component
public class ImmediateEvaluationPolicy implements IEvaluationPolicy {

  public void checkEvaluationNeeded(NotificationQuery query) {
    // always allow, i.e. don't throw any exception
  }

}

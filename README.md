Probability based Heuristic for Predictive Business Process Monitoring
=============

Prototypical implementation for the submission: Kristof Böhmer and Stefanie Rinderle-Ma: Probability based Heuristic for Predictive Business Process Monitoring. 

The given prototypical implementation of the presented unsupervised predictive monitoring heuristic was applied during the evaluation of the submission. It analyzes process execution logs to create a probability based prediction model which enables to predict the unfolding of ongoing process executions. Further, it contains additional algorithms which were only utilized during the evaluation (e.g., to analyze the prediction performance). Finally, it holds a helper project with contains auxiliary functions which ease the handling of collections but also additional supportive data structures and algorithms, for example, to determine the similarity of multiple given execution traces. The main projects are:

ReadData
---------
The implementation was split into multiple projects. First, the **ReadData** project which enables to read and prepare information from XES and CSV based logs as input for the following steps. Hereby, it especially focuses on real life logs, i.e., BPIC 2012 and Helpdesk logs. Further details on these log sources are given in the paper. Note, each log utilizes different parameter names and approaches to encode aspects, such as, the start and end of activity executions. Hence, the respective log source can be configured based on the DataSource enumeration as BPIC or Helpdesk. 


Config
---------
Secondly, the **Config** project holds all the relevant configurations. For example, the data separation configuration which controls how many percent of the logged traces are used as training or testing data. Note, each individual evaluated prediction task is configured in the ConfigEvaluation.java file. Simply control which prediction task is performed by commenting/uncommenting the appropriate code regions.


Model Propabilistic
---------
Secondly, the  **Model Propabilistic** project which holds all the relevant code to create the probability based prediction models utilized by the proposed approach. For example, it holds classes which enable to represent activities and their probability/weight. Further, it holds the main prediction logic. For example, how to create the prediction model, how to determine the probability of each potential future, and so on. For this it incorporates, for example, other sub projects such as the Helper project which holds the proposed extension of the Damerau–Levenshtein distance metric. Note, in this project you can also control if the extension should be used or not which becomes relevant when performing timestamp vs. activity predictions. 

Pred
---------
Thirdly, the  **Pred** project which brings all the different projects together. For example, it contains the different classes which execute the activity and timestamp predictions based on the configuration given in the Config project. Based on the generated results the Evaluation project is utilized to determine the prediction quality. For this the accuracy is measured for the next execution event activity prediction (i.e., how frequently is the predicted and the real activity equal). Note, the evaluation was found to be deterministic, i.e., each run of the evaluation resulted in generating the same outcome.



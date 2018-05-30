var app = angular.module("voiceApp", []);
app
		.controller(
				"voiceAppController",
				function($scope, $http) {
					$scope.addSpace = function(text) {
						return text.split('_').join(' ');
					};

					console.log('controller() started');
					$scope.error = 0;
					$scope.showHeader = false;
					$scope.Unknown = '';
					var interim_text = '';
					var final_text = '';
					$scope.question = '';
					$scope.input_state = 'search-input-red';
					var ignore = true;
					// if (false) {
					if ('webkitSpeechRecognition' in window) {
						var recognition = new webkitSpeechRecognition();
						recognition.continuous = true;
						recognition.interimResults = true;
						recognition.lang = 'en-in';

						var grammar = '#JSGF V1.0; grammar colors; public <color> = castorama stores | sumangal | CE | opco | blue | brown | chocolate | coral | crimson | cyan | fuchsia | ghostwhite | gold | goldenrod | gray | green | indigo | ivory | khaki | lavender | lime | linen | magenta | maroon | moccasin | navy | olive | orange | orchid | peru | pink | plum | purple | red | salmon | sienna | silver | snow | tan | teal | thistle | tomato | turquoise | violet | white | yellow ;'
						var speechRecognitionList = new webkitSpeechGrammarList();
						speechRecognitionList.addFromString(grammar, 1);
						recognition.grammars = speechRecognitionList;

						recognition.start();

						recognition.onstart = function() {
							console.log('onstart() speak now');
						};

						recognition.onerror = function(event) {
							console.log('onerror() ' + event.error);
						};

						recognition.onend = function() {
							console.log('onend() end');
							final_text = '';
							interim_text = '';
							$scope.input_state = 'search-input-red';
							ignore = true;
							$scope.$apply();
							recognition.start();
						};

						recognition.onresult = function(event) {
							interim_text = '';
							for (var i = event.resultIndex; i < event.results.length; ++i) {
								console.log(event.results[i][0].transcript);
								if (!ignore) {
									if (event.results[i].isFinal) {
										final_text += event.results[i][0].transcript;
										console.log('process >> ' + final_text);
										var question = (final_text).trim()
												.toUpperCase();
										if (question.startsWith('OLIVIA')) {
											question = question.substring(7);
										}
										$scope.question = question;
										$scope.$apply();
										final_text = '';
										interim_text = '';
										if (question.startsWith('OLIVIA')) {
											question = question.substring(7);
										}
										if (question != '') {
											console.log('/QueryProcessor?text='
													+ question);
											$http(
													{
														method : 'GET',
														url : '/QueryProcessor?text='
																+ question
													})
													.then(
															function successCallback(
																	response) {
																console
																		.log(response.data);
																// var data =
																// JSON.parse(response.data);
																var data = response.data;
																if (data.Result == undefined) {
																	$scope.error = 1;
																	$scope.errortext = data.Error;
																} else {
																	$scope.error = 0;
																	$scope.result = data.Result;
																}
																if (data.Header == "1")
																	$scope.showHeader = true;
																else
																	$scope.showHeader = false;
																$scope.Unknown = data.Unknown;
																console
																		.log('going to sleep');
																final_text = '';
																interim_text = '';
																$scope.input_state = 'search-input-red';
																ignore = true;

																var text = '';
																if (data.Result != undefined) {
																	if ($scope.showHeader) {
																		for (var a = 0; a < data.Result.length; a++) {
																			for (key in data.Result[a]) {
																				text += " "
																						+ key
																								.toString()
																								.replace(
																										new RegExp(
																												'_',
																												'g'),
																										' ')
																						+ " "
																						+ data.Result[a][key]
																								.toString()
																								.replace(
																										new RegExp(
																												'_',
																												'g'),
																										' ')
																						+ ",";
																			}
																		}
																	} else {
																		for (var a = 0; a < data.Result.length; a++) {
																			for (key in data.Result[a]) {
																				text += " "
																						+ data.Result[a][key]
																								.toString()
																								.replace(
																										new RegExp(
																												'_',
																												'g'),
																										' ')
																						+ ",";
																			}
																		}
																	}
																}
																if ($scope.error == 1) {
																	text += "Sorry I could not understand that. ";
																	if ($scope.Unknown != '') {
																		text += "Words not matched in the context are "
																				+ $scope.Unknown;
																	}
																}
																if ($scope.error == 0) {
																	if ($scope.Unknown != '') {
																		text += ". I tried my best but not sure about this result. Words not matched in the context are "
																				+ $scope.Unknown;
																	}
																}
																var msg = new SpeechSynthesisUtterance(
																		text);
																window.speechSynthesis
																		.speak(msg);
															},
															function errorCallback(
																	response) {
																console
																		.log(response);
															});
										}
									} else {
										interim_text += event.results[i][0].transcript;
										var question = (final_text + ' ' + interim_text)
												.trim().toUpperCase();
										if (question.startsWith('OLIVIA')) {
											question = question.substring(7);
										}
										$scope.question = question;
										$scope.$apply();
									}
								} else {
									console.log((event.results[i][0].transcript
											.toUpperCase())
											.startsWith('OLIVIA'));
									if ((event.results[i][0].transcript.trim()
											.toUpperCase())
											.startsWith('OLIVIA')) {
										$scope.input_state = 'search-input-green';
										ignore = false;
										$scope.$apply();
										var msg = new SpeechSynthesisUtterance(
												"Yes");
										window.speechSynthesis
												.speak(msg);
									}
								}
							}
						};
					}
				});
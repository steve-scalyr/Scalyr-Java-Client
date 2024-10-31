/*
 * Scalyr client library
 * Copyright 2012 Scalyr, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.ncg.scalyr;

/**
 * Exception thrown when an operation does not complete within a specified deadline.
 */
public class ScalyrDeadlineException extends ScalyrException {
  /**
   * @param deadlineInMs Time allowed for the operation (in milliseconds).
   */
  public ScalyrDeadlineException(long deadlineInMs) {
    this("Operation", deadlineInMs);
  }

  /**
   * @param operationName Name of the operation that exceeded its deadline.
   * @param deadlineInMs Time allowed for the operation (in milliseconds).
   */
  public ScalyrDeadlineException(String operationName, long deadlineInMs) {
    super(operationName + " did not complete within the specified deadline of " + deadlineInMs + " milliseconds");
  }
}
/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.alert.api.condition;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.TimeUnit;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class FrequencyCondition extends ComparisonBasedAccumulatorCondition {

    public enum Operator {
        LT, LTE, GTE, GT
    }

    private final Operator operator;

    private final Integer threshold;

    @JsonCreator
    private FrequencyCondition(
            @JsonProperty(value = "operator", required = true) Operator operator,
            @JsonProperty(value = "threshold", required = true) Integer threshold,
            @JsonProperty(value = "comparison", required = true) SingleValueCondition comparison,
            @JsonProperty(value = "duration", required = true) long duration,
            @JsonProperty(value = "timeUnit") TimeUnit timeUnit) {
        super(Type.FREQUENCY, comparison, timeUnit, duration);

        this.operator = operator;
        this.threshold = threshold;
    }

    public Operator getOperator() {
        return operator;
    }

    public Integer getThreshold() {
        return threshold;
    }

    public static ComparisonBuilder of(SingleValueCondition comparison) {
        return new ComparisonBuilder(comparison);
    }

    public static class ComparisonBuilder {

        private final SingleValueCondition comparison;

        ComparisonBuilder(SingleValueCondition comparison) {
            this.comparison = comparison;
        }

        public DurationBuilder duration(long duration, TimeUnit timeUnit) {
            return new DurationBuilder( comparison, duration, timeUnit);
        }

        public DurationBuilder duration(long duration) {
            return new DurationBuilder(comparison, duration);
        }
    }

    public static class DurationBuilder {
        private final SingleValueCondition comparison;
        private final long duration;
        private final TimeUnit timeUnit;

        DurationBuilder(SingleValueCondition comparison, long duration, TimeUnit timeUnit) {
            this.comparison = comparison;
            this.duration = duration;
            this.timeUnit = timeUnit;
        }

        DurationBuilder(SingleValueCondition comparison, long duration) {
            this(comparison, duration, null);
        }

        public ConditionBuilder lowerThan(Integer threshold) {
            return new ConditionBuilder(comparison, Operator.LT, threshold, duration, timeUnit);
        }

        public ConditionBuilder lowerThanOrEquals(Integer threshold) {
            return new ConditionBuilder(comparison, Operator.LTE, threshold, duration, timeUnit);
        }

        public ConditionBuilder greaterThanOrEquals(Integer threshold) {
            return new ConditionBuilder(comparison, Operator.GTE, threshold, duration, timeUnit);
        }

        public ConditionBuilder greaterThan(Integer threshold) {
            return new ConditionBuilder(comparison, Operator.GT, threshold, duration, timeUnit);
        }
    }

    public static class ConditionBuilder {
        private final SingleValueCondition comparison;
        private final long duration;
        private final TimeUnit timeUnit;
        private final Operator operator;
        private final Integer threshold;

        ConditionBuilder(SingleValueCondition comparison, Operator operator, Integer threshold, long duration, TimeUnit timeUnit) {
            this.comparison = comparison;
            this.duration = duration;
            this.timeUnit = timeUnit;
            this.operator = operator;
            this.threshold = threshold;
        }

        public FrequencyCondition build() {
            return new FrequencyCondition(
                    operator,
                    threshold,
                    comparison,
                    duration,
                    timeUnit);
        }
    }
}

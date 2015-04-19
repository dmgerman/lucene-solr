begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.search.join
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * How to aggregate multiple child hit scores into a single parent score.  */
end_comment

begin_enum
DECL|enum|ScoreMode
specifier|public
enum|enum
name|ScoreMode
block|{
comment|/**    * Do no scoring.    */
DECL|enum constant|None
name|None
block|,
comment|/**    * Parent hit's score is the average of all child scores.    */
DECL|enum constant|Avg
name|Avg
block|,
comment|/**    * Parent hit's score is the max of all child scores.    */
DECL|enum constant|Max
name|Max
block|,
comment|/**    * Parent hit's score is the sum of all child scores.    */
DECL|enum constant|Total
name|Total
block|,
comment|/**    * Parent hit's score is the min of all child scores.    */
DECL|enum constant|Min
name|Min
block|}
end_enum

end_unit


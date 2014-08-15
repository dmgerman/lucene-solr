begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.demo.facet
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|demo
operator|.
name|facet
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|Version
import|;
end_import

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Hold various constants used by facet examples.  *   * @lucene.experimental  */
end_comment

begin_interface
DECL|interface|FacetExamples
specifier|public
interface|interface
name|FacetExamples
block|{
comment|// :Post-Release-Update-Version.LUCENE_XY:
comment|/** The Lucene {@link Version} used by the example code. */
DECL|field|EXAMPLES_VER
specifier|public
specifier|static
specifier|final
name|Version
name|EXAMPLES_VER
init|=
name|Version
operator|.
name|LUCENE_5_0_0
decl_stmt|;
block|}
end_interface

end_unit


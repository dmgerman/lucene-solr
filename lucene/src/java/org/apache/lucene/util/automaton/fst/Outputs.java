begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.util.automaton.fst
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|automaton
operator|.
name|fst
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|DataOutput
import|;
end_import

begin_comment
comment|/**  * Represents the outputs for an FST, providing the basic  * algebra needed for the FST.  * @lucene.experimental  */
end_comment

begin_class
DECL|class|Outputs
specifier|public
specifier|abstract
class|class
name|Outputs
parameter_list|<
name|T
parameter_list|>
block|{
comment|// TODO: maybe change this API to allow for re-use of the
comment|// output instances -- this is an insane amount of garbage
comment|// (new object per byte/char/int) if eg used during
comment|// analysis
comment|/** Eg common("foo", "foobar") -> "foo" */
DECL|method|common
specifier|public
specifier|abstract
name|T
name|common
parameter_list|(
name|T
name|output1
parameter_list|,
name|T
name|output2
parameter_list|)
function_decl|;
comment|/** Eg subtract("foobar", "foo") -> "bar" */
DECL|method|subtract
specifier|public
specifier|abstract
name|T
name|subtract
parameter_list|(
name|T
name|output
parameter_list|,
name|T
name|inc
parameter_list|)
function_decl|;
comment|/** Eg add("foo", "bar") -> "foobar" */
DECL|method|add
specifier|public
specifier|abstract
name|T
name|add
parameter_list|(
name|T
name|prefix
parameter_list|,
name|T
name|output
parameter_list|)
function_decl|;
DECL|method|write
specifier|public
specifier|abstract
name|void
name|write
parameter_list|(
name|T
name|output
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|read
specifier|public
specifier|abstract
name|T
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/** NOTE: this output is compared with == so you must    *  ensure that all methods return the single object if    *  it's really no output */
DECL|method|getNoOutput
specifier|public
specifier|abstract
name|T
name|getNoOutput
parameter_list|()
function_decl|;
DECL|method|outputToString
specifier|public
specifier|abstract
name|String
name|outputToString
parameter_list|(
name|T
name|output
parameter_list|)
function_decl|;
DECL|method|merge
specifier|public
name|T
name|merge
parameter_list|(
name|T
name|first
parameter_list|,
name|T
name|second
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

end_unit


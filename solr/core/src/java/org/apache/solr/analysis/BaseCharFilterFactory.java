begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|util
operator|.
name|Version
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/** * * @since Solr 1.4 * */
end_comment

begin_class
DECL|class|BaseCharFilterFactory
specifier|public
specifier|abstract
class|class
name|BaseCharFilterFactory
implements|implements
name|CharFilterFactory
block|{
DECL|field|log
specifier|public
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|BaseCharFilterFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|luceneMatchVersion
specifier|protected
name|Version
name|luceneMatchVersion
decl_stmt|;
comment|/** The init args */
DECL|field|args
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
decl_stmt|;
DECL|method|getArgs
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getArgs
parameter_list|()
block|{
return|return
name|args
return|;
block|}
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|args
parameter_list|)
block|{
name|this
operator|.
name|args
operator|=
name|args
expr_stmt|;
block|}
DECL|method|setLuceneMatchVersion
specifier|public
name|void
name|setLuceneMatchVersion
parameter_list|(
name|Version
name|luceneMatchVersion
parameter_list|)
block|{
name|this
operator|.
name|luceneMatchVersion
operator|=
name|luceneMatchVersion
expr_stmt|;
block|}
DECL|method|getInt
specifier|protected
name|int
name|getInt
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getInt
argument_list|(
name|name
argument_list|,
operator|-
literal|1
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|getInt
specifier|protected
name|int
name|getInt
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|defaultVal
parameter_list|)
block|{
return|return
name|getInt
argument_list|(
name|name
argument_list|,
name|defaultVal
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getInt
specifier|protected
name|int
name|getInt
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|defaultVal
parameter_list|,
name|boolean
name|useDefault
parameter_list|)
block|{
name|String
name|s
init|=
name|args
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|useDefault
condition|)
return|return
name|defaultVal
return|;
throw|throw
operator|new
name|InitializationException
argument_list|(
literal|"Configuration Error: missing parameter '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
end_class

end_unit


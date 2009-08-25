begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.queryParser.standard.processors
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|standard
operator|.
name|processors
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|document
operator|.
name|DateField
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
name|document
operator|.
name|DateTools
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
name|document
operator|.
name|DateTools
operator|.
name|Resolution
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
name|queryParser
operator|.
name|core
operator|.
name|QueryNodeException
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
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|FieldConfig
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
name|queryParser
operator|.
name|core
operator|.
name|config
operator|.
name|QueryConfigHandler
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|ParametricQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|ParametricRangeQueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|QueryNode
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
name|queryParser
operator|.
name|core
operator|.
name|nodes
operator|.
name|ParametricQueryNode
operator|.
name|CompareOperator
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
name|queryParser
operator|.
name|core
operator|.
name|processors
operator|.
name|QueryNodeProcessorImpl
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
name|queryParser
operator|.
name|standard
operator|.
name|config
operator|.
name|DateResolutionAttribute
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
name|queryParser
operator|.
name|standard
operator|.
name|config
operator|.
name|LocaleAttribute
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
name|queryParser
operator|.
name|standard
operator|.
name|config
operator|.
name|RangeCollatorAttribute
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
name|queryParser
operator|.
name|standard
operator|.
name|nodes
operator|.
name|RangeQueryNode
import|;
end_import

begin_comment
comment|/**  * This processor converts {@link ParametricRangeQueryNode} objects to  * {@link RangeQueryNode} objects. It reads the lower and upper bounds value  * from the {@link ParametricRangeQueryNode} object and try to parse their  * values using a {@link DateFormat}. If the values cannot be parsed to a date  * value, it will only create the {@link RangeQueryNode} using the non-parsed  * values.<br/>  *<br/>  * If a {@link LocaleAttribute} is defined in the {@link QueryConfigHandler} it  * will be used to parse the date, otherwise {@link Locale#getDefault()} will be  * used.<br/>  *<br/>  * If a {@link DateResolutionAttribute} is defined and the {@link Resolution} is  * not<code>null</code> it will also be used to parse the date value.<br/>  *<br/>  * This processor will also try to retrieve a {@link RangeCollatorAttribute}  * from the {@link QueryConfigHandler}. If a {@link RangeCollatorAttribute} is  * found and the {@link Collator} is not<code>null</code>, it's set on the  * {@link RangeQueryNode}.<br/>  *   * @see RangeCollatorAttribute  * @see DateResolutionAttribute  * @see LocaleAttribute  * @see RangeQueryNode  * @see ParametricRangeQueryNode  */
end_comment

begin_class
DECL|class|ParametricRangeQueryNodeProcessor
specifier|public
class|class
name|ParametricRangeQueryNodeProcessor
extends|extends
name|QueryNodeProcessorImpl
block|{
DECL|method|ParametricRangeQueryNodeProcessor
specifier|public
name|ParametricRangeQueryNodeProcessor
parameter_list|()
block|{
comment|// empty constructor
block|}
DECL|method|postProcessNode
specifier|protected
name|QueryNode
name|postProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
if|if
condition|(
name|node
operator|instanceof
name|ParametricRangeQueryNode
condition|)
block|{
name|ParametricRangeQueryNode
name|parametricRangeNode
init|=
operator|(
name|ParametricRangeQueryNode
operator|)
name|node
decl_stmt|;
name|ParametricQueryNode
name|upper
init|=
name|parametricRangeNode
operator|.
name|getUpperBound
argument_list|()
decl_stmt|;
name|ParametricQueryNode
name|lower
init|=
name|parametricRangeNode
operator|.
name|getLowerBound
argument_list|()
decl_stmt|;
name|Locale
name|locale
init|=
name|Locale
operator|.
name|getDefault
argument_list|()
decl_stmt|;
name|Collator
name|collator
init|=
literal|null
decl_stmt|;
name|DateTools
operator|.
name|Resolution
name|dateRes
init|=
literal|null
decl_stmt|;
name|boolean
name|inclusive
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|getQueryConfigHandler
argument_list|()
operator|.
name|hasAttribute
argument_list|(
name|RangeCollatorAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|collator
operator|=
operator|(
operator|(
name|RangeCollatorAttribute
operator|)
name|getQueryConfigHandler
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|RangeCollatorAttribute
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getRangeCollator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|getQueryConfigHandler
argument_list|()
operator|.
name|hasAttribute
argument_list|(
name|LocaleAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|locale
operator|=
operator|(
operator|(
name|LocaleAttribute
operator|)
name|getQueryConfigHandler
argument_list|()
operator|.
name|getAttribute
argument_list|(
name|LocaleAttribute
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getLocale
argument_list|()
expr_stmt|;
block|}
name|FieldConfig
name|fieldConfig
init|=
name|getQueryConfigHandler
argument_list|()
operator|.
name|getFieldConfig
argument_list|(
name|parametricRangeNode
operator|.
name|getField
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldConfig
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|fieldConfig
operator|.
name|hasAttribute
argument_list|(
name|DateResolutionAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|dateRes
operator|=
operator|(
operator|(
name|DateResolutionAttribute
operator|)
name|fieldConfig
operator|.
name|getAttribute
argument_list|(
name|DateResolutionAttribute
operator|.
name|class
argument_list|)
operator|)
operator|.
name|getDateResolution
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|upper
operator|.
name|getOperator
argument_list|()
operator|==
name|CompareOperator
operator|.
name|LE
condition|)
block|{
name|inclusive
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|lower
operator|.
name|getOperator
argument_list|()
operator|==
name|CompareOperator
operator|.
name|GE
condition|)
block|{
name|inclusive
operator|=
literal|true
expr_stmt|;
block|}
name|String
name|part1
init|=
name|lower
operator|.
name|getTextAsString
argument_list|()
decl_stmt|;
name|String
name|part2
init|=
name|upper
operator|.
name|getTextAsString
argument_list|()
decl_stmt|;
try|try
block|{
name|DateFormat
name|df
init|=
name|DateFormat
operator|.
name|getDateInstance
argument_list|(
name|DateFormat
operator|.
name|SHORT
argument_list|,
name|locale
argument_list|)
decl_stmt|;
name|df
operator|.
name|setLenient
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Date
name|d1
init|=
name|df
operator|.
name|parse
argument_list|(
name|part1
argument_list|)
decl_stmt|;
name|Date
name|d2
init|=
name|df
operator|.
name|parse
argument_list|(
name|part2
argument_list|)
decl_stmt|;
if|if
condition|(
name|inclusive
condition|)
block|{
comment|// The user can only specify the date, not the time, so make sure
comment|// the time is set to the latest possible time of that date to really
comment|// include all documents:
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
decl_stmt|;
name|cal
operator|.
name|setTime
argument_list|(
name|d2
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|HOUR_OF_DAY
argument_list|,
literal|23
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MINUTE
argument_list|,
literal|59
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|SECOND
argument_list|,
literal|59
argument_list|)
expr_stmt|;
name|cal
operator|.
name|set
argument_list|(
name|Calendar
operator|.
name|MILLISECOND
argument_list|,
literal|999
argument_list|)
expr_stmt|;
name|d2
operator|=
name|cal
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dateRes
operator|==
literal|null
condition|)
block|{
comment|// no default or field specific date resolution has been set,
comment|// use deprecated DateField to maintain compatibilty with
comment|// pre-1.9 Lucene versions.
name|part1
operator|=
name|DateField
operator|.
name|dateToString
argument_list|(
name|d1
argument_list|)
expr_stmt|;
name|part2
operator|=
name|DateField
operator|.
name|dateToString
argument_list|(
name|d2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|part1
operator|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|d1
argument_list|,
name|dateRes
argument_list|)
expr_stmt|;
name|part2
operator|=
name|DateTools
operator|.
name|dateToString
argument_list|(
name|d2
argument_list|,
name|dateRes
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// do nothing
block|}
name|lower
operator|.
name|setText
argument_list|(
name|part1
argument_list|)
expr_stmt|;
name|upper
operator|.
name|setText
argument_list|(
name|part2
argument_list|)
expr_stmt|;
return|return
operator|new
name|RangeQueryNode
argument_list|(
name|lower
argument_list|,
name|upper
argument_list|,
name|collator
argument_list|)
return|;
block|}
return|return
name|node
return|;
block|}
DECL|method|preProcessNode
specifier|protected
name|QueryNode
name|preProcessNode
parameter_list|(
name|QueryNode
name|node
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|node
return|;
block|}
DECL|method|setChildrenOrder
specifier|protected
name|List
argument_list|<
name|QueryNode
argument_list|>
name|setChildrenOrder
parameter_list|(
name|List
argument_list|<
name|QueryNode
argument_list|>
name|children
parameter_list|)
throws|throws
name|QueryNodeException
block|{
return|return
name|children
return|;
block|}
block|}
end_class

end_unit


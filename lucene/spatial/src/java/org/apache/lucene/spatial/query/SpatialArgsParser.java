begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.spatial.query
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|query
package|;
end_package

begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|exception
operator|.
name|InvalidShapeException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|shape
operator|.
name|Shape
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_comment
comment|/**  * Parses a string that usually looks like "OPERATION(SHAPE)" into a {@link SpatialArgs}  * object. The set of operations supported are defined in {@link SpatialOperation}, such  * as "Intersects" being a common one. The shape portion is defined by WKT {@link com.spatial4j.core.io.WktShapeParser},  * but it can be overridden/customized via {@link #parseShape(String, com.spatial4j.core.context.SpatialContext)}.  * There are some optional name-value pair parameters that follow the closing parenthesis.  Example:  *<pre>  *   Intersects(ENVELOPE(-10,-8,22,20)) distErrPct=0.025  *</pre>  *<p/>  * In the future it would be good to support something at least semi-standardized like a  * variant of<a href="http://docs.geoserver.org/latest/en/user/filter/ecql_reference.html#spatial-predicate">  *   [E]CQL</a>.  *  * @lucene.experimental  */
end_comment

begin_class
DECL|class|SpatialArgsParser
specifier|public
class|class
name|SpatialArgsParser
block|{
DECL|field|DIST_ERR_PCT
specifier|public
specifier|static
specifier|final
name|String
name|DIST_ERR_PCT
init|=
literal|"distErrPct"
decl_stmt|;
DECL|field|DIST_ERR
specifier|public
specifier|static
specifier|final
name|String
name|DIST_ERR
init|=
literal|"distErr"
decl_stmt|;
comment|/** Writes a close approximation to the parsed input format. */
DECL|method|writeSpatialArgs
specifier|static
name|String
name|writeSpatialArgs
parameter_list|(
name|SpatialArgs
name|args
parameter_list|)
block|{
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
name|args
operator|.
name|getOperation
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|str
operator|.
name|append
argument_list|(
name|args
operator|.
name|getShape
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|getDistErrPct
argument_list|()
operator|!=
literal|null
condition|)
name|str
operator|.
name|append
argument_list|(
literal|" distErrPct="
argument_list|)
operator|.
name|append
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"%.2f%%"
argument_list|,
name|args
operator|.
name|getDistErrPct
argument_list|()
operator|*
literal|100d
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|getDistErr
argument_list|()
operator|!=
literal|null
condition|)
name|str
operator|.
name|append
argument_list|(
literal|" distErr="
argument_list|)
operator|.
name|append
argument_list|(
name|args
operator|.
name|getDistErr
argument_list|()
argument_list|)
expr_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|str
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Parses a string such as "Intersects(ENVELOPE(-10,-8,22,20)) distErrPct=0.025".    *    * @param v   The string to parse. Mandatory.    * @param ctx The spatial context. Mandatory.    * @return Not null.    * @throws IllegalArgumentException if the parameters don't make sense or an add-on parameter is unknown    * @throws ParseException If there is a problem parsing the string    * @throws InvalidShapeException When the coordinates are invalid for the shape    */
DECL|method|parse
specifier|public
name|SpatialArgs
name|parse
parameter_list|(
name|String
name|v
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
throws|throws
name|ParseException
throws|,
name|InvalidShapeException
block|{
name|int
name|idx
init|=
name|v
operator|.
name|indexOf
argument_list|(
literal|'('
argument_list|)
decl_stmt|;
name|int
name|edx
init|=
name|v
operator|.
name|lastIndexOf
argument_list|(
literal|')'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
argument_list|<
literal|0
operator|||
name|idx
argument_list|>
name|edx
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"missing parens: "
operator|+
name|v
argument_list|,
operator|-
literal|1
argument_list|)
throw|;
block|}
name|SpatialOperation
name|op
init|=
name|SpatialOperation
operator|.
name|get
argument_list|(
name|v
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|body
init|=
name|v
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|,
name|edx
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|body
operator|.
name|length
argument_list|()
operator|<
literal|1
condition|)
block|{
throw|throw
operator|new
name|ParseException
argument_list|(
literal|"missing body : "
operator|+
name|v
argument_list|,
name|idx
operator|+
literal|1
argument_list|)
throw|;
block|}
name|Shape
name|shape
init|=
name|parseShape
argument_list|(
name|body
argument_list|,
name|ctx
argument_list|)
decl_stmt|;
name|SpatialArgs
name|args
init|=
name|newSpatialArgs
argument_list|(
name|op
argument_list|,
name|shape
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|.
name|length
argument_list|()
operator|>
operator|(
name|edx
operator|+
literal|1
operator|)
condition|)
block|{
name|body
operator|=
name|v
operator|.
name|substring
argument_list|(
name|edx
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|body
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|aa
init|=
name|parseMap
argument_list|(
name|body
argument_list|)
decl_stmt|;
name|readNameValuePairs
argument_list|(
name|args
argument_list|,
name|aa
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|aa
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unused parameters: "
operator|+
name|aa
argument_list|)
throw|;
block|}
block|}
block|}
name|args
operator|.
name|validate
argument_list|()
expr_stmt|;
return|return
name|args
return|;
block|}
DECL|method|newSpatialArgs
specifier|protected
name|SpatialArgs
name|newSpatialArgs
parameter_list|(
name|SpatialOperation
name|op
parameter_list|,
name|Shape
name|shape
parameter_list|)
block|{
return|return
operator|new
name|SpatialArgs
argument_list|(
name|op
argument_list|,
name|shape
argument_list|)
return|;
block|}
DECL|method|readNameValuePairs
specifier|protected
name|void
name|readNameValuePairs
parameter_list|(
name|SpatialArgs
name|args
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|nameValPairs
parameter_list|)
block|{
name|args
operator|.
name|setDistErrPct
argument_list|(
name|readDouble
argument_list|(
name|nameValPairs
operator|.
name|remove
argument_list|(
name|DIST_ERR_PCT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|.
name|setDistErr
argument_list|(
name|readDouble
argument_list|(
name|nameValPairs
operator|.
name|remove
argument_list|(
name|DIST_ERR
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|parseShape
specifier|protected
name|Shape
name|parseShape
parameter_list|(
name|String
name|str
parameter_list|,
name|SpatialContext
name|ctx
parameter_list|)
throws|throws
name|ParseException
block|{
comment|//return ctx.readShape(str);//still in Spatial4j 0.4 but will be deleted
return|return
name|ctx
operator|.
name|readShapeFromWkt
argument_list|(
name|str
argument_list|)
return|;
block|}
DECL|method|readDouble
specifier|protected
specifier|static
name|Double
name|readDouble
parameter_list|(
name|String
name|v
parameter_list|)
block|{
return|return
name|v
operator|==
literal|null
condition|?
literal|null
else|:
name|Double
operator|.
name|valueOf
argument_list|(
name|v
argument_list|)
return|;
block|}
DECL|method|readBool
specifier|protected
specifier|static
name|boolean
name|readBool
parameter_list|(
name|String
name|v
parameter_list|,
name|boolean
name|defaultValue
parameter_list|)
block|{
return|return
name|v
operator|==
literal|null
condition|?
name|defaultValue
else|:
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|v
argument_list|)
return|;
block|}
comment|/** Parses "a=b c=d f" (whitespace separated) into name-value pairs. If there    * is no '=' as in 'f' above then it's short for f=f. */
DECL|method|parseMap
specifier|protected
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parseMap
parameter_list|(
name|String
name|body
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|body
argument_list|,
literal|" \n\t"
argument_list|)
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|a
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|int
name|idx
init|=
name|a
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
name|String
name|k
init|=
name|a
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
decl_stmt|;
name|String
name|v
init|=
name|a
operator|.
name|substring
argument_list|(
name|idx
operator|+
literal|1
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|v
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|a
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
block|}
end_class

end_unit


begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Necessary classes to implement query builders.  *  *<h2>Query Parser Builders</h2>  *<p>  * The package<tt>org.apache.lucene.queryParser.builders</tt> contains the interface that  * builders must implement, it also contain a utility {@link org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder}, which walks the tree  * and call the Builder for each node in the tree.  * Builder normally convert QueryNode Object into a Lucene Query Object,  * and normally it's a one-to-one mapping class.  *  * But other builders implementations can by written to convert QueryNode objects to other non lucene objects.  */
end_comment

begin_package
DECL|package|org.apache.lucene.queryparser.flexible.core.builders
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryparser
operator|.
name|flexible
operator|.
name|core
operator|.
name|builders
package|;
end_package

end_unit


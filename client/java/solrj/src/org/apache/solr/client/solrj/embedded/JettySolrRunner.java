begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.client.solrj.embedded
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|embedded
package|;
end_package

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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServlet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
operator|.
name|SolrDispatchFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
operator|.
name|SolrServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|servlet
operator|.
name|SolrUpdateServlet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Handler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Server
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|servlet
operator|.
name|FilterHolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|log
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * Run solr using jetty  *   * @version $Id$  * @since solr 1.3  */
end_comment

begin_class
DECL|class|JettySolrRunner
specifier|public
class|class
name|JettySolrRunner
block|{
DECL|field|server
name|Server
name|server
decl_stmt|;
DECL|field|dispatchFilter
name|FilterHolder
name|dispatchFilter
decl_stmt|;
DECL|method|JettySolrRunner
specifier|public
name|JettySolrRunner
parameter_list|(
name|String
name|context
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|this
operator|.
name|init
argument_list|(
name|context
argument_list|,
name|port
argument_list|)
expr_stmt|;
block|}
comment|//  public JettySolrRunner( String context, String home, String dataDir, int port, boolean log )
comment|//  {
comment|//    if(!log) {
comment|//      System.setProperty("org.mortbay.log.class", NoLog.class.getName() );
comment|//      System.setProperty("java.util.logging.config.file", home+"/conf/logging.properties");
comment|//      NoLog noLogger = new NoLog();
comment|//      org.mortbay.log.Log.setLog(noLogger);
comment|//    }
comment|//
comment|//    // Initalize JNDI
comment|//    Config.setInstanceDir(home);
comment|//    new SolrCore(dataDir, new IndexSchema(home+"/conf/schema.xml"));
comment|//    this.init( context, port );
comment|//  }
DECL|method|init
specifier|private
name|void
name|init
parameter_list|(
name|String
name|context
parameter_list|,
name|int
name|port
parameter_list|)
block|{
name|server
operator|=
operator|new
name|Server
argument_list|(
name|port
argument_list|)
expr_stmt|;
name|server
operator|.
name|setStopAtShutdown
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Initialize the servlets
name|Context
name|root
init|=
operator|new
name|Context
argument_list|(
name|server
argument_list|,
name|context
argument_list|,
name|Context
operator|.
name|SESSIONS
argument_list|)
decl_stmt|;
name|root
operator|.
name|addServlet
argument_list|(
name|SolrServlet
operator|.
name|class
argument_list|,
literal|"/select"
argument_list|)
expr_stmt|;
name|root
operator|.
name|addServlet
argument_list|(
name|SolrUpdateServlet
operator|.
name|class
argument_list|,
literal|"/update"
argument_list|)
expr_stmt|;
comment|// for some reason, there must be a servlet for this to get applied
name|root
operator|.
name|addServlet
argument_list|(
name|Servlet404
operator|.
name|class
argument_list|,
literal|"/*"
argument_list|)
expr_stmt|;
name|dispatchFilter
operator|=
name|root
operator|.
name|addFilter
argument_list|(
name|SolrDispatchFilter
operator|.
name|class
argument_list|,
literal|"*"
argument_list|,
name|Handler
operator|.
name|REQUEST
argument_list|)
expr_stmt|;
block|}
comment|//------------------------------------------------------------------------------------------------
comment|//------------------------------------------------------------------------------------------------
DECL|method|start
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|server
operator|.
name|isRunning
argument_list|()
condition|)
block|{
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|stop
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|server
operator|.
name|isRunning
argument_list|()
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|//--------------------------------------------------------------
comment|//--------------------------------------------------------------
comment|/**     * This is a stupid hack to give jetty something to attach to    */
DECL|class|Servlet404
specifier|public
specifier|static
class|class
name|Servlet404
extends|extends
name|HttpServlet
block|{
annotation|@
name|Override
DECL|method|service
specifier|public
name|void
name|service
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|res
parameter_list|)
throws|throws
name|IOException
block|{
name|res
operator|.
name|sendError
argument_list|(
literal|404
argument_list|,
literal|"Can not find: "
operator|+
name|req
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_class
DECL|class|NoLog
class|class
name|NoLog
implements|implements
name|Logger
block|{
DECL|field|debug
specifier|private
specifier|static
name|boolean
name|debug
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"DEBUG"
argument_list|,
literal|null
argument_list|)
operator|!=
literal|null
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|method|NoLog
specifier|public
name|NoLog
parameter_list|()
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|NoLog
specifier|public
name|NoLog
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
operator|==
literal|null
condition|?
literal|""
else|:
name|name
expr_stmt|;
block|}
DECL|method|isDebugEnabled
specifier|public
name|boolean
name|isDebugEnabled
parameter_list|()
block|{
return|return
name|debug
return|;
block|}
DECL|method|setDebugEnabled
specifier|public
name|void
name|setDebugEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|debug
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|info
specifier|public
name|void
name|info
parameter_list|(
name|String
name|msg
parameter_list|,
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{}
DECL|method|debug
specifier|public
name|void
name|debug
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|th
parameter_list|)
block|{}
DECL|method|debug
specifier|public
name|void
name|debug
parameter_list|(
name|String
name|msg
parameter_list|,
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{}
DECL|method|warn
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|msg
parameter_list|,
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{}
DECL|method|warn
specifier|public
name|void
name|warn
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|th
parameter_list|)
block|{}
DECL|method|getLogger
specifier|public
name|Logger
name|getLogger
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|(
name|name
operator|==
literal|null
operator|&&
name|this
operator|.
name|name
operator|==
literal|null
operator|)
operator|||
operator|(
name|name
operator|!=
literal|null
operator|&&
name|name
operator|.
name|equals
argument_list|(
name|this
operator|.
name|name
argument_list|)
operator|)
condition|)
return|return
name|this
return|;
return|return
operator|new
name|NoLog
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"NOLOG["
operator|+
name|name
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit


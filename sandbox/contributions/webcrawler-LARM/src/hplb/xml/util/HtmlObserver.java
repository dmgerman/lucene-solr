begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * $Id$  *   * Copyright 1997 Hewlett-Packard Company  *   * This file may be copied, modified and distributed only in  * accordance with the terms of the limited licence contained  * in the accompanying file LICENSE.TXT.  */
end_comment

begin_package
DECL|package|hplb.xml.util
package|package
name|hplb
operator|.
name|xml
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_comment
comment|/**  * A callback interface used in conjunction with UrlScanner. Allows actions  * to be taken whenever the scanner finds a URL in an HTML document. The  * scanner knows about most HTML 4.0 elements which can contain URLs.  * Can be used, for example, to implement robot code which crawls a hypertext  * graph. This interface is similar to Jeff Poskanzer's Acme.HtmlObserver.  *   * @see     HtmlScanner  * @author  Anders Kristensen  */
end_comment

begin_interface
DECL|interface|HtmlObserver
specifier|public
interface|interface
name|HtmlObserver
block|{
comment|/** Invoked when the scanner finds an&lt;a href=""&gt; URL. */
DECL|method|gotAHref
specifier|public
name|void
name|gotAHref
parameter_list|(
name|String
name|urlStr
parameter_list|,
name|URL
name|contextUrl
parameter_list|,
name|Object
name|data
parameter_list|)
function_decl|;
comment|/** Invoked when the scanner finds an&lt;img src=""&gt; URL. */
DECL|method|gotImgSrc
specifier|public
name|void
name|gotImgSrc
parameter_list|(
name|String
name|urlStr
parameter_list|,
name|URL
name|contextUrl
parameter_list|,
name|Object
name|data
parameter_list|)
function_decl|;
comment|/** Invoked when the scanner finds a&lt;base href=""&gt; URL. */
DECL|method|gotBaseHref
specifier|public
name|void
name|gotBaseHref
parameter_list|(
name|String
name|urlStr
parameter_list|,
name|URL
name|contextUrl
parameter_list|,
name|Object
name|data
parameter_list|)
function_decl|;
comment|/** Invoked when the scanner finds a&lt;area href=""&gt; URL. */
DECL|method|gotAreaHref
specifier|public
name|void
name|gotAreaHref
parameter_list|(
name|String
name|urlStr
parameter_list|,
name|URL
name|contextUrl
parameter_list|,
name|Object
name|data
parameter_list|)
function_decl|;
comment|/** Invoked when the scanner finds an&lt;frame src=""&gt; URL. */
DECL|method|gotFrameSrc
specifier|public
name|void
name|gotFrameSrc
parameter_list|(
name|String
name|urlStr
parameter_list|,
name|URL
name|contextUrl
parameter_list|,
name|Object
name|data
parameter_list|)
function_decl|;
block|}
end_interface

end_unit


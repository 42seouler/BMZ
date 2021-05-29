package com.hotechcourse.oauth.security.oauth2.user;

import java.util.Map;

public class FtSeoulOAuth2UserInfo extends OAuth2UserInfo {

  public FtSeoulOAuth2UserInfo(Map<String, Object> attributes) {
    super(attributes);
  }

  @Override
  public String getId() {
    return ((Integer) attributes.get("id")).toString();
  }

  @Override
  public String getName() {
    return (String) attributes.get("login");
  }

  @Override
  public String getEmail() {
    return (String) attributes.get("email");
  }

  @Override
  public String getImageUrl() {
    return (String) attributes.get("image_url");
  }
}

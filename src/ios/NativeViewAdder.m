/********* cordova-outsystems-nativeview-adder.m Cordova Plugin Implementation *******/

#import <Cordova/CDV.h>


@interface NativeViewAdder : CDVPlugin {
    NSString * callback;
    NSMutableDictionary* addedViews;
}

- (void)addButton:(CDVInvokedUrlCommand*)command;
@end

@implementation NativeViewAdder

- (void)pluginInitialize{
    addedViews = [[NSMutableDictionary alloc] init];
}

- (void)addButton:(CDVInvokedUrlCommand*)command
{
    callback = command.callbackId;
    NSString* configsString = [command.arguments objectAtIndex:0];
    NSError *error = nil;
    NSMutableDictionary* configs = [NSJSONSerialization JSONObjectWithData:[configsString dataUsingEncoding:NSUTF8StringEncoding] options:kNilOptions error:&error];
    if (configs != nil && configs.count > 0) {
        
        NSString *uuid = [[NSUUID UUID] UUIDString];
        
        UIButton *button = [UIButton buttonWithType:UIButtonTypeCustom];
        [button addTarget:self
                   action:@selector(onClick:)
         forControlEvents:UIControlEventTouchUpInside];
        [button setTitle:[configs objectForKey:@"title"] forState:UIControlStateNormal];
        
        UIColor* backgroundColor;
        if ([configs objectForKey:@"backgroundColor"] != nil) {
            backgroundColor = [self colorFromHexString:[[configs objectForKey:@"backgroundColor"] stringValue]];
        }else{
            backgroundColor = [UIColor colorNamed:@"BackgroundColor"];
        }
        [button setBackgroundColor:backgroundColor];
        button.translatesAutoresizingMaskIntoConstraints = NO;
        
        NSArray* constraints = [configs objectForKey:@"constraints"];
        
        button.titleLabel.textColor = UIColor.whiteColor;
        [button setFrame:CGRectMake(0, 0, 200, 100)];
        
        [self.webView.scrollView addSubview:button];
        
        [addedViews setObject:button forKey:uuid];
        
        if ([configs objectForKey:@"width"]) {
            NSNumber* width = [configs objectForKey:@"width"];
            CGFloat fwidth = [width floatValue];
            [[button.widthAnchor constraintEqualToConstant:fwidth] setActive:true];
        }
        
        if ([configs objectForKey:@"height"]) {
            NSNumber* height = [configs objectForKey:@"height"];
            CGFloat fheight = [height floatValue];
            [[button.heightAnchor constraintEqualToConstant:fheight] setActive:true];
        }
        
        for (NSDictionary* constraint in constraints) {
            NSString* directionString = [constraint objectForKey:@"direction"];
            NSLayoutAttribute direction;
            
            CGFloat margin = [[constraint objectForKey:@"margin"] floatValue];
            
            if ([directionString  isEqual: @"CenterX"]) {
                direction = NSLayoutAttributeCenterX;
            }else if ([directionString  isEqual: @"CenterY"]) {
                direction = NSLayoutAttributeCenterY;
            }else if ([directionString  isEqual: @"Top"]) {
                direction = NSLayoutAttributeTop;
            }else if ([directionString  isEqual: @"Bottom"]) {
                direction = NSLayoutAttributeBottom;
            }else if ([directionString  isEqual: @"Left"]) {
                direction = NSLayoutAttributeLeft;
            }else {
                direction = NSLayoutAttributeRight;
            }
            NSLayoutConstraint *newConstraint = [NSLayoutConstraint constraintWithItem:button attribute:direction relatedBy:NSLayoutRelationEqual toItem:self.webView.scrollView attribute:direction multiplier:1 constant:margin];
            [self.webView.scrollView addConstraint:newConstraint];
        }
    }
}

// Assumes input like "#00FF00" (#RRGGBB).
- (UIColor *)colorFromHexString:(NSString *)hexString {
    unsigned rgbValue = 0;
    NSScanner *scanner = [NSScanner scannerWithString:hexString];
    if ( [hexString rangeOfString:@"#"].location == 0 ){
        [scanner setScanLocation:1]; // bypass '#' character
    }else{
        [scanner setScanLocation:0];
    }
    [scanner scanHexInt:&rgbValue];
    return [UIColor colorWithRed:((rgbValue & 0xFF0000) >> 16)/255.0 green:((rgbValue & 0xFF00) >> 8)/255.0 blue:(rgbValue & 0xFF)/255.0 alpha:1.0];
}

- (void)clearViews:(CDVInvokedUrlCommand*)command
{
    //TODO remove all views
}

- (void)onClick:(UIButton *)sender{
    NSMutableDictionary * resultDictionary = [[NSMutableDictionary alloc] init];
    NSString* uuid = @"";
    
    [resultDictionary setObject:@"Click" forKey:@"Action"];
    
    for (NSString* key in addedViews) {
        if ([[addedViews objectForKey:key] isKindOfClass:[UIButton class]]) {
            UIButton *view = [addedViews objectForKey:key];
            if([view isEqual:sender]){
                uuid = key;
                break;
            }
        }
    }
    [resultDictionary setObject:uuid forKey:@"id"];
    
    CDVPluginResult *result = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultDictionary];
    [result setKeepCallbackAsBool:TRUE];
    [self.commandDelegate sendPluginResult:result callbackId:callback];
}

@end
